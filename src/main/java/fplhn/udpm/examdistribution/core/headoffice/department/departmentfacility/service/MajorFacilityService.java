package fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.service;


import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request.CreateMajorFacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request.MajorFacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request.UpdateMajorFacilityRequest;
import jakarta.validation.Valid;


public interface MajorFacilityService {

    ResponseObject<?> getAllMajorFacilities(@Valid MajorFacilityRequest request);

    ResponseObject<?> getMajorFacilityById(String majorFacilityId);

    ResponseObject<?> createMajorFacility(@Valid CreateMajorFacilityRequest request);

    ResponseObject<?> updateMajorFacility(String majorFacilityId, @Valid UpdateMajorFacilityRequest request);

    ResponseObject<?> getAllMajors(String departmentId);

}
