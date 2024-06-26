package fplhn.udpm.examdistribution.core.headoffice.staff.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRoleChangePermissionRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRoleRequest;

public interface HOStaffRoleService {

    ResponseObject<?> getAllRole(String staffId);

    ResponseObject<?> getRolesChecked(HOStaffRoleRequest roleRequest);

    ResponseObject<?> updateStaffRole(HOStaffRoleChangePermissionRequest request);

}
