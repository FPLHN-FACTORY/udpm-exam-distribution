package fplhn.udpm.examdistribution.infrastructure.config.job.staff.commonio;

import fplhn.udpm.examdistribution.entity.MajorFacility;
import fplhn.udpm.examdistribution.entity.Role;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.StaffMajorFacility;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.model.dto.TranferStaffRole;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.model.request.StaffExcelRequest;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.repository.*;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class StaffProcessor implements ItemProcessor<StaffExcelRequest, TranferStaffRole> {

    @Autowired
    private ConfigRoleCustomRepository roleCustomRepository;

    @Autowired
    private ConfigStaffCustomRepository staffCustomRepository;

    @Autowired
    private ConfigMajorFacilityCustomRepository majorFacilityCustomRepository;

    @Autowired
    private ConfigStaffMajorFacilityRepository staffMajorFacilityRepository;

    @Autowired
    private ConfigDepartmentFacilityCustomRepository departmentFacilityRepository;
    @Override
    public TranferStaffRole process(StaffExcelRequest item) throws Exception {
        try {
            String staffCode = "";
            String departmentFacility = item.getDepartmentFacilityName();
            String departmentName = departmentFacility.split(" - ")[0];
            String majorName = departmentFacility.split(" - ")[1];
            String facilityCode = departmentFacility.split(" - ")[2];
            String roleFacility = item.getRoleFacilityName();
            String roleName = roleFacility.split(" - ")[0];
            String rFacilityName = roleFacility.split(" - ")[1];
            List<MajorFacility> majorFacilities = majorFacilityCustomRepository.getMajorFacilities(departmentName, majorName, facilityCode);
            List<Role> roles = roleCustomRepository.findAllByRoleNameAndFacilityName(roleName, rFacilityName);

            if (majorFacilities.isEmpty()) {
                log.error("Chuyên ngành theo cơ sở không tồn tại");
                return null;
            }

            if (roles.isEmpty()) {
                log.error("Chức Vụ Không Tồn Tại");
                return null;
            }

            if (item.getStaffCode() == null || item.getStaffCode().isEmpty()) {
                log.error("Mã Nhân Viên Không Được Để Trống");
                return null;
            } else if (!item.getAccountFe().contains(item.getStaffCode()) || !item.getAccountFpt().contains(item.getStaffCode())) {
                log.error("ID Nhân Viên Không Đúng Định Dạng");
                return null;
            } else {
                staffCode = item.getStaffCode();
            }

            List<Staff> staffList = staffCustomRepository.findAllByStaffCode(staffCode);
            StaffMajorFacility newStaffMajorFacility = new StaffMajorFacility();

            if (!staffList.isEmpty()) {
                Staff staff = staffList.get(0);
                staff.setName(item.getName());
                staff.setAccountFpt(item.getAccountFpt());
                staff.setAccountFe(item.getAccountFe());
                staff.setStatus(EntityStatus.ACTIVE);

                List<StaffMajorFacility> staffMajorFacilities = staffMajorFacilityRepository.findAllByStaffAndFacility(staff.getId(), facilityCode);
                if (staffMajorFacilities.isEmpty()) {
                    newStaffMajorFacility.setStatus(EntityStatus.ACTIVE);
                    newStaffMajorFacility.setMajorFacility(majorFacilities.get(0));
                } else {
                    StaffMajorFacility existingStaffMajorFacility = staffMajorFacilities.get(0);
                    existingStaffMajorFacility.setStatus(EntityStatus.ACTIVE);
                    existingStaffMajorFacility.setMajorFacility(majorFacilities.get(0));
                    newStaffMajorFacility = existingStaffMajorFacility;
                }
                return new TranferStaffRole(staff, roles.get(0), newStaffMajorFacility);
            }

            Staff staffNew = new Staff();
            staffNew.setId(CodeGenerator.generateRandomCode());
            staffNew.setStaffCode(staffCode);
            staffNew.setName(item.getName());
            staffNew.setAccountFpt(item.getAccountFpt());
            staffNew.setAccountFe(item.getAccountFe());
            staffNew.setStatus(EntityStatus.ACTIVE);

            StaffMajorFacility staffMajorFacility = new StaffMajorFacility();
            staffMajorFacility.setStatus(EntityStatus.ACTIVE);
            staffMajorFacility.setMajorFacility(majorFacilities.get(0));
            return new TranferStaffRole(staffNew, roles.get(0), staffMajorFacility);
        } catch (Exception e) {
            log.error("Error processing excel row : " + item, e);
            return null;
        }
    }
}
