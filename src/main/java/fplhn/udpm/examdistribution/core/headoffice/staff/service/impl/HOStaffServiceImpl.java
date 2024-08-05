package fplhn.udpm.examdistribution.core.headoffice.staff.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOSaveStaffRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.repository.HOStaffDepartmentFacilityRepository;
import fplhn.udpm.examdistribution.core.headoffice.staff.repository.HOStaffRepository;
import fplhn.udpm.examdistribution.core.headoffice.staff.repository.HOStaffRoleRepository;
import fplhn.udpm.examdistribution.core.headoffice.staff.service.HOStaffService;
import fplhn.udpm.examdistribution.entity.HistoryImport;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.StaffRole;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.utils.Helper;
import fplhn.udpm.examdistribution.utils.HistoryLogUtils;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class HOStaffServiceImpl implements HOStaffService {

    private final HOStaffRepository staffRepo;

    private final HOStaffDepartmentFacilityRepository departmentFacilityRepo;

    private final HOStaffRoleRepository staffRoleRepo;

    private final SessionHelper sessionHelper;

    private final HistoryLogUtils historyLogUtils;

    @Override
    public ResponseObject<?> getStaffByRole(HOStaffRequest hoRoleStaffRequest) {
        Pageable page = Helper.createPageable(hoRoleStaffRequest, "createdDate");
        return new ResponseObject<>(staffRepo.getStaffs(page, hoRoleStaffRequest), HttpStatus.OK, "get staffs successfully");
    }

    @Override
    public ResponseObject<?> getDepartmentFacility() {
        return new ResponseObject<>(departmentFacilityRepo.getDepartmentFacilities(), HttpStatus.OK, "get departments facilities successfully");
    }

    @Override
    public ResponseObject<?> detailStaff(String staffId) {
        return new ResponseObject<>(staffRepo.getStaff(staffId), HttpStatus.OK, "get one staff successfully");
    }

    @Override
    public ResponseObject<?> createStaff(HOSaveStaffRequest staffRequest) {

        Staff staff = new Staff();

        staff.setName(staffRequest.getName());

        List<Staff> staffList = staffRepo.findByStaffCode(staffRequest.getStaffCode());

        if (!staffList.isEmpty() && staffList.get(0).getStatus() == EntityStatus.ACTIVE) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT, "Mã nhân viên đã tồn tại");
        }

        staff.setStatus(EntityStatus.ACTIVE);
        staff.setStaffCode(staffRequest.getStaffCode());
        staff.setAccountFe(staffRequest.getAccountFe());
        staff.setAccountFpt(staffRequest.getAccountFpt());
        staffRepo.save(staff);

        return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm nhân viên thành công");
    }

    @Override
    public ResponseObject<?> updateStaff(HOSaveStaffRequest staffRequest) {

        Optional<Staff> staffOptional = staffRepo.findById(staffRequest.getId());

        if (staffOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên");
        }

        Staff staff = staffOptional.get();
        staff.setId(staffRequest.getId());
        staff.setName(staffRequest.getName());
        List<Staff> checkStaff = staffRepo.findByStaffCode(staffRequest.getStaffCode());

        if (!checkStaff.isEmpty() &&
            !checkStaff.get(0).getId().equalsIgnoreCase(staffRequest.getStaffCode()) &&
            checkStaff.get(0).getStatus() == EntityStatus.ACTIVE) {
            if (!staff.getId().equals(checkStaff.get(0).getId())) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Mã nhân viên đã tồn tại");
            }
        }

        staff.setStaffCode(staffRequest.getStaffCode());
        staff.setAccountFe(staffRequest.getAccountFe());
        staff.setAccountFpt(staffRequest.getAccountFpt());
        staffRepo.save(staff);

        return new ResponseObject<>(null, HttpStatus.OK, "Sửa nhân viên thành công");
    }

    @Override
    public ResponseObject<?> deleteStaff(String idStaff) {
        Optional<Staff> staffOptional = staffRepo.findById(idStaff);
        if (staffOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Staff not found");
        }

        Staff staff = staffOptional.get();
        EntityStatus newStatus = staff.getStatus() == EntityStatus.ACTIVE ? EntityStatus.INACTIVE : EntityStatus.ACTIVE;

        // Change status in staff_role
        List<StaffRole> staffRoles = staffRoleRepo.findAllByStaff_Id(idStaff);
        staffRoles.stream().forEach(staffRole -> staffRole.setStatus(newStatus));
        staffRoleRepo.saveAll(staffRoles);

        // Change status in staff
        staff.setStatus(newStatus);
        staffRepo.save(staff);

        String message = newStatus == EntityStatus.INACTIVE ? "Staff deactivated successfully" : "Staff activated successfully";
        return new ResponseObject<>(null, HttpStatus.OK, message);
    }

    @Override
    public ResponseObject<?> getLogsImportStaff(int page, int size) {
        List<HistoryImport> listLogRaw = historyLogUtils.getHistoryImportByFacilityIdAndStaffId(
                sessionHelper.getCurrentUserFacilityId(), sessionHelper.getCurrentUserId()
        );
        List<HistoryImport> loggerObjects = listLogRaw.stream()
                .skip((long) page * size)
                .limit(size)
                .toList();
        return ResponseObject.successForward(
                PageableObject.of(new PageImpl<>(
                        loggerObjects, PageRequest.of(page, size), loggerObjects.size()
                )),
                "Lấy lịch sử thay đổi thành công"
        );
    }

}
