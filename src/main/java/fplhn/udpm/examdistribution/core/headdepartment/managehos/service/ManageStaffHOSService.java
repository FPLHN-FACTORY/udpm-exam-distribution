package fplhn.udpm.examdistribution.core.headdepartment.managehos.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.StaffRequest;
import jakarta.validation.Valid;

public interface ManageStaffHOSService {

    ResponseObject<?> getAllStaffs(@Valid StaffRequest request);

}
