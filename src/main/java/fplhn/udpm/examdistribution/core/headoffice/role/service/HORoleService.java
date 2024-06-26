package fplhn.udpm.examdistribution.core.headoffice.role.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.role.model.request.HORoleRequest;
import fplhn.udpm.examdistribution.core.headoffice.role.model.request.HOSaveRoleRequest;

public interface HORoleService {
    ResponseObject<?> getAllRole(HORoleRequest roleRequest);

    ResponseObject<?> getFacilities();

    ResponseObject<?> saveRole(HOSaveRoleRequest roleRequest);

    ResponseObject<?> getOneRole(String id);

    ResponseObject<?> deleteRole(String id);
}
