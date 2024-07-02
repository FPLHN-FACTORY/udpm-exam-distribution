package fplhn.udpm.examdistribution.core.headdepartment.managehos.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.AssignSubjectStaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.StaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectAssignedRequest;
import jakarta.validation.Valid;

public interface ManageStaffHOSService {

    ResponseObject<?> getAllStaffs(@Valid StaffRequest request);

    ResponseObject<?> getSubjectAssigned(@Valid SubjectAssignedRequest request);

    ResponseObject<?> assignSubjectToStaff(@Valid AssignSubjectStaffRequest request);

}
