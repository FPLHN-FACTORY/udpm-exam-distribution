package fplhn.udpm.examdistribution.core.headdepartment.managehos.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.AssignSubjectStaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.HeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.ReassignHeadOfSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.StaffsBySubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectAssignedRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectsStaffRequest;
import jakarta.validation.Valid;

public interface ManageHeadSubjectService {

    ResponseObject<?> getStaffAndHeadSubjects(@Valid HeadSubjectRequest request);

    ResponseObject<?> getSubjectAssigned(@Valid SubjectAssignedRequest request);

    ResponseObject<?> assignSubjectToStaff(@Valid AssignSubjectStaffRequest request);

}
