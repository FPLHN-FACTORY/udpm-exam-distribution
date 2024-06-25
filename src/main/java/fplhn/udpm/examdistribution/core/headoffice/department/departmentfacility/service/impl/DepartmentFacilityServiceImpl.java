package fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request.FindFacilityDetailRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request.CreateOrUpdateDepartmentFacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.repository.DFDepartmentExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.repository.DFDepartmentFacilityExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.repository.DFFacilityExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.repository.DFStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.service.DepartmentFacilityService;
import fplhn.udpm.examdistribution.entity.Department;
import fplhn.udpm.examdistribution.entity.DepartmentFacility;
import fplhn.udpm.examdistribution.entity.Facility;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class DepartmentFacilityServiceImpl implements DepartmentFacilityService {

    private final DFDepartmentFacilityExtendRepository dfDepartmentFacilityExtendRepository;

    private final DFDepartmentExtendRepository dfDepartmentExtendRepository;

    private final DFFacilityExtendRepository dfFacilityExtendRepository;

    private final DFStaffExtendRepository dfStaffExtendRepository;


    @Override
    public ResponseObject<?> getAllDepartmentFacility(String id, FindFacilityDetailRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(dfDepartmentFacilityExtendRepository.getDepartmentFacilitiesByValueFind(id, pageable, request)),
                HttpStatus.OK,
                "Lấy thành công danh sách bộ môn theo cơ sở"
        );
    }

    @Override
    public ResponseObject<?> addDepartmentFacility(@Valid CreateOrUpdateDepartmentFacilityRequest request) {
        Optional<Department> departmentOptional = dfDepartmentExtendRepository.findById(request.getDepartmentId());
        Optional<Facility> facilityOptional = dfFacilityExtendRepository.findById(request.getFacilityId());
        Optional<Staff> staffOptional = dfStaffExtendRepository.findById(request.getHeadOfDepartmentId());

        if (dfDepartmentFacilityExtendRepository.existsByIdDepartmentAndIdFacilityAndIdAdd(request).isPresent()) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Bộ môn theo cơ sở đã tồn tại!");
        }

        if (facilityOptional.isPresent() && staffOptional.isPresent() && departmentOptional.isPresent()) {
            DepartmentFacility addDepartmentFacility = new DepartmentFacility();
            addDepartmentFacility.setDepartment(departmentOptional.get());
            addDepartmentFacility.setFacility(facilityOptional.get());
            addDepartmentFacility.setStaff(staffOptional.get());
            addDepartmentFacility.setStatus(EntityStatus.ACTIVE);
            dfDepartmentFacilityExtendRepository.save(addDepartmentFacility);
            return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm thành công");
        } else if (facilityOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Không tìm thấy cơ sở trên");
        } else if (staffOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Không tìm thấy CNBM trên");
        } else {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Không tìm thấy bộ môn trên");
        }
    }

    @Override
    public ResponseObject<?> updateDepartmentFacility(@Valid CreateOrUpdateDepartmentFacilityRequest request, String id) {
        Optional<DepartmentFacility> departmentFacilityOptional = dfDepartmentFacilityExtendRepository.findById(id);
        if (departmentFacilityOptional.isPresent()) {
            Optional<Department> departmentOptional = dfDepartmentExtendRepository.findById(request.getDepartmentId());
            Optional<Facility> facilityOptional = dfFacilityExtendRepository.findById(request.getFacilityId());
            Optional<Staff> staffOptional = dfStaffExtendRepository.findById(request.getHeadOfDepartmentId());

            if (facilityOptional.isPresent() && staffOptional.isPresent() && departmentOptional.isPresent()) {
                DepartmentFacility updateDepartmentFacility = departmentFacilityOptional.get();
                if (updateDepartmentFacility.getFacility().equals(facilityOptional.get())) {
                    updateDepartmentFacility.setStaff(staffOptional.get());
                    updateDepartmentFacility.setDepartment(departmentOptional.get());
                    updateDepartmentFacility.setFacility(facilityOptional.get());
                    dfDepartmentFacilityExtendRepository.save(updateDepartmentFacility);
                } else {
                    if (isDuplicateRecord(request).isPresent()) {
                        return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Cập nhật trùng cơ sở hoặc để như cũ");
                    }
                    updateDepartmentFacility.setStaff(staffOptional.get());
                    updateDepartmentFacility.setDepartment(departmentOptional.get());
                    updateDepartmentFacility.setFacility(facilityOptional.get());
                    dfDepartmentFacilityExtendRepository.save(updateDepartmentFacility);
                }

                return new ResponseObject<>(null, HttpStatus.OK, "Sửa thành công !");
            } else if (facilityOptional.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Không tìm thấy cơ sở trên");
            } else if (staffOptional.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Không tìm thấy nhân viên trên");
            } else if (departmentOptional.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Không tìm thấy bộ môn trên");
            } else {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Sửa thất bại");
            }
        } else {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Bộ môn theo cơ sở trên không tồn tại");
        }
    }

    private Optional<DepartmentFacility> isDuplicateRecord(CreateOrUpdateDepartmentFacilityRequest request) {
        return dfDepartmentFacilityExtendRepository.existsDepartmentFacilitiesByDepartmentAndFacility(request.getDepartmentId(), request.getFacilityId());
    }

    @Override
    public ResponseObject<?> deleteDepartmentFacility(String id) {
        Optional<DepartmentFacility> departmentFacilityOptional = dfDepartmentFacilityExtendRepository.findById(id);
        if (departmentFacilityOptional.isPresent()) {
            DepartmentFacility departmentFacility = departmentFacilityOptional.get();
            departmentFacility.setStatus(
                    departmentFacility.getStatus().name().equalsIgnoreCase(EntityStatus.ACTIVE.name()) ? EntityStatus.INACTIVE : EntityStatus.ACTIVE
            );
            dfDepartmentFacilityExtendRepository.save(departmentFacility);
            return new ResponseObject<>(null, HttpStatus.OK, "Chỉnh sửa thành công bộ môn theo cơ sở");
        }

        return new ResponseObject<>(null, HttpStatus.OK, "Bộ môn theo cơ sở không tồn tại");
    }

    @Override
    public ResponseObject<?> getListFacility() {
        return new ResponseObject<>(dfDepartmentFacilityExtendRepository.getListFacility(), HttpStatus.OK, "Lấy thành công danh sách cơ sở");
    }

    @Override
    public ResponseObject<?> getListStaff() {
        return new ResponseObject<>(dfDepartmentFacilityExtendRepository.getListStaff(), HttpStatus.OK, "Lấy thành công danh sách CNBM");
    }

    @Override
    public ResponseObject<?> getDepartmentName(String departmentId) {
        Optional<Department> departmentOptional = dfDepartmentExtendRepository.findById(departmentId);
        if (departmentOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Tìm thấy bộ môn này!");
        }
        return new ResponseObject<>(departmentOptional.get().getName(), HttpStatus.OK, "Tìm thấy thành công tên bộ môn");
    }

}
