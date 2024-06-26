package fplhn.udpm.examdistribution.core.headoffice.staff.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRequest;

public interface HORoleStaffService {

    ResponseObject<?> getStaffByRole(HOStaffRequest hoRoleStaffRequest);

}
