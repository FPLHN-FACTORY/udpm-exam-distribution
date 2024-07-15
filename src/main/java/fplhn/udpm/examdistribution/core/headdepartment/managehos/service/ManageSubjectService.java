package fplhn.udpm.examdistribution.core.headdepartment.managehos.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.ReassignHeadOfSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.StaffsBySubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectsStaffRequest;
import jakarta.validation.Valid;

public interface ManageSubjectService {

    ResponseObject<?> getSubjectsStaff(@Valid SubjectsStaffRequest request);

    ResponseObject<?> reassignSubjectToStaff(@Valid ReassignHeadOfSubjectRequest request);

    ResponseObject<?> getStaffsBySubject(@Valid StaffsBySubjectRequest request);

}
