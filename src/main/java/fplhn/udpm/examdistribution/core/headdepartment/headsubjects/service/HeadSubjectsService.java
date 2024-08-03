package fplhn.udpm.examdistribution.core.headdepartment.headsubjects.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.AssignOrUnassignSubjectForHeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.HeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.HeadSubjectSearchRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.ReassignHeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.SubjectByHeadSubjectRequest;
import jakarta.validation.Valid;

public interface HeadSubjectsService {

    ResponseObject<?> getAllHeadSubjects(HeadSubjectRequest request);

    ResponseObject<?> getSubjectsByHeadSubject(String headSubjectId, SubjectByHeadSubjectRequest request);

    ResponseObject<?> getSubjectsWithAssign(String headSubjectId, SubjectByHeadSubjectRequest request);

    ResponseObject<?> assignSubjectForHeadSubject(
            String headSubjectId,
            @Valid AssignOrUnassignSubjectForHeadSubjectRequest request
    );

    ResponseObject<?> unassignSubjectForHeadSubject(
            String headSubjectId,
            @Valid AssignOrUnassignSubjectForHeadSubjectRequest request
    );

    ResponseObject<?> reassignSubjectForAnotherHeadSubject(@Valid ReassignHeadSubjectRequest request);

    ResponseObject<?> searchStaff(HeadSubjectSearchRequest request);

}
