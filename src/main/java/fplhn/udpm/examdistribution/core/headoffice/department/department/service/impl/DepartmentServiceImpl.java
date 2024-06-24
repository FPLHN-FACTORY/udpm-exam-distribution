package fplhn.udpm.examdistribution.core.headoffice.department.department.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.CreateUpdateDepartmentRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.FindDepartmentsRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.response.DepartmentResponse;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.response.ListDepartmentResponse;
import fplhn.udpm.examdistribution.core.headoffice.department.department.repository.DepartmentExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.department.department.service.DepartmentService;
import fplhn.udpm.examdistribution.entity.Department;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentExtendRepository departmentExtendRepository;

    @Override
    public PageableObject<DepartmentResponse> getAllDepartment(FindDepartmentsRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Page<DepartmentResponse> res = departmentExtendRepository.getAllDepartmentByFilter(pageable, request);
        return new PageableObject<>(res);
    }

    @Override
    public ResponseObject<?> getDetailDepartment(String id) {
        return new ResponseObject<>(departmentExtendRepository.getDetailDepartment(id), HttpStatus.OK, "Lấy thành công bộ môn");
    }

    @Override
    public ResponseObject<?> addDepartment(@Valid CreateUpdateDepartmentRequest request) {
        request.setDepartmentName(request.getDepartmentName().replaceAll("\\s+", " "));
        request.setDepartmentCode(request.getDepartmentCode().replaceAll("\\s+", " "));

        boolean checkExistName = departmentExtendRepository.existsByName(request.getDepartmentName().trim());
        boolean checkExistCode = departmentExtendRepository.existsByCode(request.getDepartmentCode().trim());

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
        this.departmentExtendRepository.save(department);
        return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm thành công bộ môn");
    }

    @Override
    public ResponseObject<?> updateDepartment(@Valid CreateUpdateDepartmentRequest request, String id) {
        request.setDepartmentName(request.getDepartmentName().replaceAll("\\s+", " "));
        request.setDepartmentCode(request.getDepartmentCode().replaceAll("\\s+", " "));

        Optional<Department> departmentOptional = departmentExtendRepository.findById(id);

        if (departmentOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Bộ môn không tồn tại");
        }

        Department updateDepartment = departmentOptional.get();
        if (!updateDepartment.getCode().trim().equalsIgnoreCase(request.getDepartmentCode().trim())) {
            if (departmentExtendRepository.existsByCode(request.getDepartmentCode().trim())) {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Mã bộ môn đã tồn tại: " +
                                                                             request.getDepartmentCode().trim());
            }
        }
        if (!updateDepartment.getName().trim().equalsIgnoreCase(request.getDepartmentName().trim())) {
            if (departmentExtendRepository.existsByName(request.getDepartmentName().trim())) {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Tên bộ môn đã tồn tại: " +
                                                                             request.getDepartmentName().trim());
            }
        }

        updateDepartment.setCode(request.getDepartmentCode().trim());
        updateDepartment.setName(request.getDepartmentName().trim());
        this.departmentExtendRepository.save(updateDepartment);
        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật bộ môn thành công");
    }

    @Override
    public ResponseObject<?> deleteDepartment(String id) {
        Optional<Department> departmentOptional = departmentExtendRepository.findById(id);

        if (departmentOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Bộ môn không tồn tại");
        }

        Department deleteDepartment = departmentOptional.get();
        deleteDepartment.setStatus(
                deleteDepartment.getStatus().name().equalsIgnoreCase(EntityStatus.ACTIVE.name())
                        ? EntityStatus.INACTIVE : EntityStatus.ACTIVE
        );

        this.departmentExtendRepository.save(deleteDepartment);
        return new ResponseObject<>(null, HttpStatus.OK, "Xóa bộ môn thành công");
    }

    @Override
    public List<ListDepartmentResponse> getListDepartment() {
        return departmentExtendRepository.getListDepartment();
    }

}
