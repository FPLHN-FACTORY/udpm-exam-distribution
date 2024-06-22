package fplhn.udpm.examdistribution.core.headoffice.subject.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.subject.model.request.CreateUpdateSubjectRequest;
import fplhn.udpm.examdistribution.core.headoffice.subject.model.request.SubjectRequest;
import jakarta.validation.Valid;

public interface SubjectService {

    ResponseObject<?> getAllSubject(SubjectRequest request);

    ResponseObject<?> createSubject(@Valid CreateUpdateSubjectRequest request);

    ResponseObject<?> updateSubject(String subjectId, @Valid CreateUpdateSubjectRequest request);

    ResponseObject<?> changeSubjectStatus(String subjectId);

    ResponseObject<?> getSubjectById(String subjectId);

}
