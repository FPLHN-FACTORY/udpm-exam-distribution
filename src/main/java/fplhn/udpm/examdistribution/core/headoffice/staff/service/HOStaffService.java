package fplhn.udpm.examdistribution.core.headoffice.staff.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOSaveStaffRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRequest;

public interface HOStaffService {

    ResponseObject<?> getStaffByRole(HOStaffRequest hoRoleStaffRequest);

    ResponseObject<?> getDepartmentFacility();

    ResponseObject<?> detailStaff(String staffId);

    ResponseObject<?> createStaff(HOSaveStaffRequest staffRequest);

    ResponseObject<?> updateStaff(HOSaveStaffRequest staffRequest);

    ResponseObject<?> deleteStaff(String idStaff);

}
