package fplhn.udpm.examdistribution.core.headoffice.department.department.service;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.CreateUpdateDepartmentRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.FindDepartmentsRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.response.DepartmentResponse;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.response.ListDepartmentResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface DepartmentService {

    PageableObject<DepartmentResponse> getAllDepartment(FindDepartmentsRequest request);

    ResponseObject<?> getDetailDepartment(String id);

    ResponseObject<?> addDepartment(@Valid CreateUpdateDepartmentRequest request);

    ResponseObject<?> updateDepartment(@Valid CreateUpdateDepartmentRequest request, String id);

    ResponseObject<?> deleteDepartment(String id);

    List<ListDepartmentResponse> getListDepartment();

}
