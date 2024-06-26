package fplhn.udpm.examdistribution.core.headoffice.staff.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOSaveStaffRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.repository.HOStaffDepartmentFacilityRepository;
import fplhn.udpm.examdistribution.core.headoffice.staff.repository.HOStaffRepository;
import fplhn.udpm.examdistribution.core.headoffice.staff.service.HOStaffService;
import fplhn.udpm.examdistribution.entity.DepartmentFacility;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.utils.Helper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HOStaffServiceImpl implements HOStaffService {

    private HOStaffRepository staffRepo;

    private HOStaffDepartmentFacilityRepository departmentFacilityRepo;

    public HOStaffServiceImpl(HOStaffRepository staffRepo, HOStaffDepartmentFacilityRepository departmentFacilityRepo) {
        this.staffRepo = staffRepo;
        this.departmentFacilityRepo = departmentFacilityRepo;
    }

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
        if (!staffRepo.findByStaffCode(staffRequest.getStaffCode()).isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT, "staff code already exists");
        }
        staff.setStatus(EntityStatus.ACTIVE);
        staff.setStaffCode(staffRequest.getStaffCode());
        staff.setAccountFe(staffRequest.getAccountFe());
        staff.setAccountFpt(staffRequest.getAccountFpt());
        Optional<DepartmentFacility> departmentFacility = departmentFacilityRepo.findById(staffRequest.getDepartmentFacilityId());
        staff.setDepartmentFacility(departmentFacility.isPresent() ? departmentFacility.get() : null);
        staffRepo.save(staff);
        return new ResponseObject<>(null, HttpStatus.CREATED, "create staff successfully");
    }

    @Override
    public ResponseObject<?> updateStaff(HOSaveStaffRequest staffRequest) {
        Optional<Staff> staffOptional = staffRepo.findById(staffRequest.getId());
        if (staffOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "staff not found");
        }
        Staff staff = staffOptional.get();
        staff.setId(staffRequest.getId());
        staff.setName(staffRequest.getName());
        List<Staff> checkStaff = staffRepo.findByStaffCode(staffRequest.getStaffCode());
        if (!checkStaff.isEmpty() && !checkStaff.get(0).getId().equalsIgnoreCase(staffRequest.getStaffCode())) {
            return new ResponseObject<>(null, HttpStatus.OK, "staff code is conflict with other staff");
        }
        staff.setStaffCode(staffRequest.getStaffCode());
        staff.setAccountFe(staffRequest.getAccountFe());
        staff.setAccountFpt(staffRequest.getAccountFpt());
        Optional<DepartmentFacility> departmentFacility = departmentFacilityRepo.findById(staffRequest.getDepartmentFacilityId());
        staff.setDepartmentFacility(departmentFacility.isPresent() ? departmentFacility.get() : null);
        staffRepo.save(staff);
        return new ResponseObject<>(null, HttpStatus.OK, "create staff successfully");
    }

    @Override
    public ResponseObject<?> deleteStaff(String idStaff) {
        Optional<Staff> staffOptional = staffRepo.findById(idStaff);
        if (staffOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "staff not found");
        }
        staffOptional.get().setStatus(EntityStatus.INACTIVE);
        staffRepo.save(staffOptional.get());
        return new ResponseObject<>(null, HttpStatus.OK, "delete staff successfully");
    }

}
