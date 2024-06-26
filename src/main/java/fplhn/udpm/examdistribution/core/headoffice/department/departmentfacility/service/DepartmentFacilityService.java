package fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request.FindFacilityDetailRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request.CreateOrUpdateDepartmentFacilityRequest;
import jakarta.validation.Valid;

public interface DepartmentFacilityService {

    ResponseObject<?> getAllDepartmentFacility(String id, FindFacilityDetailRequest request);

    ResponseObject<?> addDepartmentFacility(@Valid CreateOrUpdateDepartmentFacilityRequest request);

    ResponseObject<?> updateDepartmentFacility(@Valid CreateOrUpdateDepartmentFacilityRequest request, String id);

    ResponseObject<?> deleteDepartmentFacility(String id);

    ResponseObject<?> getListFacility();

    ResponseObject<?> getListStaff();

    ResponseObject<?> getDepartmentName(String departmentId);

}
