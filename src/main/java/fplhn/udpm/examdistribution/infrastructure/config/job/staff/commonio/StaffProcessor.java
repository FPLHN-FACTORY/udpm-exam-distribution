package fplhn.udpm.examdistribution.infrastructure.config.job.staff.commonio;

import fplhn.udpm.examdistribution.entity.MajorFacility;
import fplhn.udpm.examdistribution.entity.Role;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.StaffMajorFacility;
import fplhn.udpm.examdistribution.infrastructure.config.global.GlobalVariables;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.model.dto.TransferStaffRole;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.model.request.StaffExcelRequest;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.repository.ConfigMajorFacilityCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.repository.ConfigRoleCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.repository.ConfigStaffCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.utils.HistoryLogUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class StaffProcessor implements ItemProcessor<StaffExcelRequest, TransferStaffRole> {

    @Setter(onMethod_ = {@Autowired})
    private ConfigRoleCustomRepository roleCustomRepository;

    @Setter(onMethod_ = {@Autowired})
    private ConfigStaffCustomRepository staffCustomRepository;

    @Setter(onMethod_ = {@Autowired})
    private ConfigMajorFacilityCustomRepository majorFacilityCustomRepository;

    @Setter(onMethod_ = {@Autowired})
    private HistoryLogUtils historyLogUtils;

    @Setter
    private String fileName;

    @Setter(onMethod_ = {@Autowired})
    private GlobalVariables globalVariables;

    @Override
    public TransferStaffRole process(StaffExcelRequest item) throws Exception {
        try {
            String staffCode = "";
            String departmentFacility = item.getDepartmentFacilityName();
            String departmentName = departmentFacility.split(" - ")[0];
            String majorName = departmentFacility.split(" - ")[1];
            String facilityCode = departmentFacility.split(" - ")[2];
            String roleFacility = item.getRoleFacilityName();
            String roleName = roleFacility.split(" - ")[0];
            String rFacilityName = roleFacility.split(" - ")[1];
            List<MajorFacility> majorFacilities = majorFacilityCustomRepository
                    .getMajorFacilities(departmentName, majorName, facilityCode);
            List<Role> roles = roleCustomRepository
                    .findAllByRoleNameAndFacilityName(roleName, rFacilityName);

            if (majorFacilities.isEmpty()) {
                log.error("Chuyên ngành theo cơ sở không tồn tại");
                logErrorRecordToCSV(
                        "Lỗi bản ghi số " + item.getOrderNumber() + ": Chuyên ngành theo cơ sở không tồn tại"
                );
                return null;
            }

            if (roles.isEmpty()) {
                log.error("Chức Vụ Không Tồn Tại");
                logErrorRecordToCSV(
                        "Lỗi bản ghi số " + item.getOrderNumber() + ": Chức vụ không tồn tại"
                );
                return null;
            }

            if (item.getStaffCode() == null || item.getStaffCode().isEmpty()) {
                log.error("Mã Nhân Viên Không Được Để Trống");
                logErrorRecordToCSV(
                        "Lỗi bản ghi số " + item.getOrderNumber() + ": Mã nhân viên không được để trống"
                );
                return null;
            } else if (
                    !item
                            .getAccountFe()
                            .contains(item.getStaffCode()) ||
                    !item
                            .getAccountFpt()
                            .contains(item.getStaffCode())
            ) {
                log.error("Account Nhân Viên Không Đúng Định Dạng{} {} {}", item.getStaffCode(), item.getAccountFe(), item.getAccountFpt());
                logErrorRecordToCSV(
                        "Lỗi bản ghi số " + item.getOrderNumber() + ": Account nhân viên không đúng định dạng"
                );
                return null;
            } else {
                staffCode = item.getStaffCode();
            }

            List<Staff> staffList = staffCustomRepository
                    .findAllByStaffCodeAndStatus(staffCode, EntityStatus.ACTIVE);
            if (!staffList.isEmpty()) {
                log.error("Mã nhân viên đã tồn tại");
                logErrorRecordToCSV(
                        "Lỗi bản ghi số " + item.getOrderNumber() + ": Mã nhân viên đã tồn tại"
                );
                return null;
            }

            Staff staffNew = new Staff();
            staffNew.setId(UUID.randomUUID().toString());
            staffNew.setStaffCode(staffCode);
            staffNew.setName(item.getName());
            staffNew.setAccountFpt(item.getAccountFpt());
            staffNew.setAccountFe(item.getAccountFe());
            staffNew.setStatus(EntityStatus.ACTIVE);

            StaffMajorFacility staffMajorFacility = new StaffMajorFacility();
            staffMajorFacility.setStatus(EntityStatus.ACTIVE);
            staffMajorFacility.setMajorFacility(majorFacilities.get(0));
            return new TransferStaffRole(staffNew, roles.get(0), staffMajorFacility);
        } catch (Exception e) {
            log.error("Error processing excel row : {}", item, e);
            logErrorRecordToCSV(
                    "Lỗi tại bản ghi số " + item.getOrderNumber() + ": " + e.getMessage()
            );
            return null;
        }
    }

    private void logErrorRecordToCSV(String content) {
        historyLogUtils.logErrorRecord(
                content,
                fileName,
                (String) globalVariables.getGlobalVariable(SessionConstant.CURRENT_USER_ID),
                (String) globalVariables.getGlobalVariable(SessionConstant.CURRENT_USER_FACILITY_ID)
        );
    }

}