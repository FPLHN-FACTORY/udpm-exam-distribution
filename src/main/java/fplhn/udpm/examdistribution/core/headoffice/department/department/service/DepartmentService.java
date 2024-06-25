package fplhn.udpm.examdistribution.core.headoffice.department.department.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.CreateUpdateDepartmentRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.FindDepartmentsRequest;
import jakarta.validation.Valid;

public interface DepartmentService {

    ResponseObject<?> getAllDepartment(FindDepartmentsRequest request);

    ResponseObject<?> getDetailDepartment(String id);

    ResponseObject<?> addDepartment(@Valid CreateUpdateDepartmentRequest request);

    ResponseObject<?> updateDepartment(@Valid CreateUpdateDepartmentRequest request, String id);

    ResponseObject<?> deleteDepartment(String id);

    ResponseObject<?> getListDepartment();

}
