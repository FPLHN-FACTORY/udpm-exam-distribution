package fplhn.udpm.examdistribution.core.headdepartment.headsubjects.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.AssignSubjectForHeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.HeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.SubjectByHeadSubjectRequest;
import jakarta.validation.Valid;

public interface HeadSubjectsService {

    ResponseObject<?> getAllHeadSubjects(HeadSubjectRequest request);

    ResponseObject<?> getSubjectsByHeadSubject(SubjectByHeadSubjectRequest request);

    ResponseObject<?> getSubjectsWithAssign(SubjectByHeadSubjectRequest request);

    ResponseObject<?> assignSubjectForHeadSubject(String headSubjectId, @Valid AssignSubjectForHeadSubjectRequest request);

}
