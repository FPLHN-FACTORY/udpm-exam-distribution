package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.AssignSubjectGroupToStaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.HeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.SubjectGroupAssignedRequest;
import jakarta.validation.Valid;

public interface ManageHeadSubjectService {

    ResponseObject<?> getStaffAndHeadSubjects(@Valid HeadSubjectRequest request);

    ResponseObject<?> getSubjectGroupAssigned(@Valid SubjectGroupAssignedRequest request);

    ResponseObject<?> assignSubjectGroupToStaff(@Valid AssignSubjectGroupToStaffRequest request);

    ResponseObject<?> getChangeHistory(int page, int size);

}
