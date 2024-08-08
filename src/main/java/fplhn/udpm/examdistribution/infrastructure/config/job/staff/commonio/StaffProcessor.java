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
import fplhn.udpm.examdistribution.infrastructure.constant.LogFileType;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.utils.HistoryLogUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
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
            String[] departmentParts = item.getDepartmentFacilityName().split(" - ");
            String[] roleParts = item.getRoleFacilityName().split(" - ");

            if (departmentParts.length != 3 || roleParts.length != 2) {
                return handleError("Department or role information is incomplete.", item);
            }

            String departmentName = departmentParts[0];
            String majorName = departmentParts[1];
            String facilityCode = departmentParts[2];
            String roleName = roleParts[0];
            String rFacilityName = roleParts[1];

            Optional<MajorFacility> majorFacility = fetchMajorFacility(departmentName, majorName, facilityCode);
            Optional<Role> role = fetchRole(roleName, rFacilityName);

            if (majorFacility.isEmpty() || role.isEmpty()) {
                return handleError("Major facility or role not found.", item);
            }

            if (!isValidStaffCode(item)) {
                return handleError("Invalid or missing staff code.", item);
            }

            return createTransferStaffRole(item, majorFacility.get(), role.get());
        } catch (Exception e) {
            return handleError("Processing error: " + e.getMessage(), item);
        }
    }

    private Optional<MajorFacility> fetchMajorFacility(String departmentName, String majorName, String facilityCode) {
        List<MajorFacility> majorFacilities = majorFacilityCustomRepository.getMajorFacilities(departmentName, majorName, facilityCode);
        return majorFacilities.isEmpty() ? Optional.empty() : Optional.of(majorFacilities.get(0));
    }

    private Optional<Role> fetchRole(String roleName, String facilityName) {
        List<Role> roles = roleCustomRepository.findAllByRoleNameAndFacilityName(roleName, facilityName);
        return roles.isEmpty() ? Optional.empty() : Optional.of(roles.get(0));
    }

    private boolean isValidStaffCode(StaffExcelRequest item) {
        String staffCode = item.getStaffCode();
        if (staffCode == null || staffCode.isEmpty()) {
            return false;
        }
        return item.getAccountFe().toLowerCase().contains(staffCode.toLowerCase()) &&
               item.getAccountFpt().toLowerCase().contains(staffCode.toLowerCase());
    }

    private TransferStaffRole createTransferStaffRole(StaffExcelRequest item, MajorFacility majorFacility, Role role) {
        Staff staff = staffCustomRepository
                .findByStaffCodeAndStatus(item.getStaffCode(), EntityStatus.ACTIVE)
                .map(existingStaff -> updateExistingStaff(existingStaff, item))
                .orElseGet(() -> createNewStaff(item));

        StaffMajorFacility staffMajorFacility = new StaffMajorFacility();
        staffMajorFacility.setStatus(EntityStatus.ACTIVE);
        staffMajorFacility.setMajorFacility(majorFacility);

        return new TransferStaffRole(staff, role, staffMajorFacility);
    }

    private Staff updateExistingStaff(Staff staff, StaffExcelRequest item) {
        staff.setName(item.getName());
        staff.setAccountFpt(item.getAccountFpt());
        staff.setAccountFe(item.getAccountFe());
        return staff;
    }

    private Staff createNewStaff(StaffExcelRequest item) {
        Staff newStaff = new Staff();
        newStaff.setId(UUID.randomUUID().toString());
        newStaff.setStaffCode(item.getStaffCode());
        newStaff.setName(item.getName());
        newStaff.setAccountFpt(item.getAccountFpt());
        newStaff.setAccountFe(item.getAccountFe());
        newStaff.setStatus(EntityStatus.ACTIVE);
        return newStaff;
    }

    private TransferStaffRole handleError(String errorMessage, StaffExcelRequest item) {
        log.error("{} Record: {}", errorMessage, item);
        logErrorRecordToCSV("Lỗi bản ghi số " + item.getOrderNumber() + ": " + errorMessage);
        return null;
    }

    private void logErrorRecordToCSV(String content) {
        historyLogUtils.logErrorRecord(
                content,
                fileName,
                (String) globalVariables.getGlobalVariable(SessionConstant.CURRENT_USER_ID),
                (String) globalVariables.getGlobalVariable(SessionConstant.CURRENT_USER_FACILITY_ID),
                LogFileType.STAFF
        );
    }
}
