package fplhn.udpm.examdistribution.core.headoffice.department.department.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.CreateUpdateDepartmentRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.FindDepartmentsRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.department.repository.DPDepartmentExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.department.department.service.DepartmentService;
import fplhn.udpm.examdistribution.entity.Department;
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
public class DepartmentServiceImpl implements DepartmentService {

    private final DPDepartmentExtendRepository DPDepartmentExtendRepository;

    @Override
    public ResponseObject<?> getAllDepartment(FindDepartmentsRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(DPDepartmentExtendRepository.getAllDepartmentByFilter(pageable, request)),
                HttpStatus.OK,
                "Lấy thành công danh sách bộ môn"
        );
    }

    @Override
    public ResponseObject<?> getDetailDepartment(String id) {
        return new ResponseObject<>(DPDepartmentExtendRepository.getDetailDepartment(id), HttpStatus.OK, "Lấy thành công bộ môn");
    }

    @Override
    public ResponseObject<?> addDepartment(@Valid CreateUpdateDepartmentRequest request) {
        request.setDepartmentName(request.getDepartmentName().replaceAll("\\s+", " "));
        request.setDepartmentCode(request.getDepartmentCode().replaceAll("\\s+", " "));

        boolean checkExistName = DPDepartmentExtendRepository.existsByName(request.getDepartmentName().trim());
        boolean checkExistCode = DPDepartmentExtendRepository.existsByCode(request.getDepartmentCode().trim());

        if (checkExistCode) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Mã bộ môn đã tồn tại");
        }

        if (checkExistName) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Tên bộ môn đã tồn tại");
        }

        Department department = new Department();
        department.setStatus(EntityStatus.ACTIVE);
        department.setCode(request.getDepartmentCode().trim());
        department.setName(request.getDepartmentName().trim());
        this.DPDepartmentExtendRepository.save(department);
        return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm thành công bộ môn");
    }

    @Override
    public ResponseObject<?> updateDepartment(@Valid CreateUpdateDepartmentRequest request, String id) {
        request.setDepartmentName(request.getDepartmentName().replaceAll("\\s+", " "));
        request.setDepartmentCode(request.getDepartmentCode().replaceAll("\\s+", " "));

        Optional<Department> departmentOptional = DPDepartmentExtendRepository.findById(id);

        if (departmentOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Bộ môn không tồn tại");
        }

        Department updateDepartment = departmentOptional.get();
        if (!updateDepartment.getCode().trim().equalsIgnoreCase(request.getDepartmentCode().trim())) {
            if (DPDepartmentExtendRepository.existsByCode(request.getDepartmentCode().trim())) {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Mã bộ môn đã tồn tại: " +
                                                                             request.getDepartmentCode().trim());
            }
        }
        if (!updateDepartment.getName().trim().equalsIgnoreCase(request.getDepartmentName().trim())) {
            if (DPDepartmentExtendRepository.existsByName(request.getDepartmentName().trim())) {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Tên bộ môn đã tồn tại: " +
                                                                             request.getDepartmentName().trim());
            }
        }

        updateDepartment.setCode(request.getDepartmentCode().trim());
        updateDepartment.setName(request.getDepartmentName().trim());
        this.DPDepartmentExtendRepository.save(updateDepartment);
        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật bộ môn thành công");
    }

    @Override
    public ResponseObject<?> deleteDepartment(String id) {
        Optional<Department> departmentOptional = DPDepartmentExtendRepository.findById(id);

        if (departmentOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Bộ môn không tồn tại");
        }

        Department deleteDepartment = departmentOptional.get();
        deleteDepartment.setStatus(
                deleteDepartment.getStatus().name().equalsIgnoreCase(EntityStatus.ACTIVE.name())
                        ? EntityStatus.INACTIVE : EntityStatus.ACTIVE
        );

        this.DPDepartmentExtendRepository.save(deleteDepartment);
        return new ResponseObject<>(null, HttpStatus.OK, "Xóa bộ môn thành công");
    }

    @Override
    public ResponseObject<?> getListDepartment() {
        return new ResponseObject<>(DPDepartmentExtendRepository.getListDepartment(), HttpStatus.OK, "Lấy thành công danh sách bộ môn");
    }

}
