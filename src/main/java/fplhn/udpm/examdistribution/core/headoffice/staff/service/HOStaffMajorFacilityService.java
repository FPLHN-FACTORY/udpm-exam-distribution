package fplhn.udpm.examdistribution.core.headoffice.staff.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffMajorFacilityRequest;
import jakarta.validation.Valid;

public interface HOStaffMajorFacilityService {

    ResponseObject<?> getStaffMajorFacilities(String staffId);

    ResponseObject<?> getDepartmentByFacility(String idFacility);

    ResponseObject<?> getMajorByDepartmentFacility(String idDepartment,String idFacility);

    ResponseObject<?> createStaffMajorFacility(@Valid HOStaffMajorFacilityRequest staffMajorFacilityRequest);

    ResponseObject<?> updateStaffMajorFacility(@Valid HOStaffMajorFacilityRequest staffMajorFacilityRequest);

    ResponseObject<?> deleteStaffMajorFacility(String idStaffMajorFacility);

    ResponseObject<?> detailStaffMajorFacility(String idStaffMajorFacility);

}
