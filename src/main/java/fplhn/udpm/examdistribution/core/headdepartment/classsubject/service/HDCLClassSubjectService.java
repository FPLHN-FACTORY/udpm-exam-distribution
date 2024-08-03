package fplhn.udpm.examdistribution.core.headdepartment.classsubject.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.request.ClassSubjectKeywordRequest;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.request.ClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.request.CreateUpdateClassSubjectRequest;
import jakarta.validation.Valid;

public interface HDCLClassSubjectService {

    ResponseObject<?> getAllClassSubject(ClassSubjectRequest request);

    ResponseObject<?> getAllClassSubjectByKeyword(ClassSubjectKeywordRequest request);

    ResponseObject<?> createClassSubject(@Valid CreateUpdateClassSubjectRequest request);

    ResponseObject<?> updateClassSubject(String classSubjectId, @Valid CreateUpdateClassSubjectRequest request);

    ResponseObject<?> changeClassSubjectStatus(String classSubjectId);

    ResponseObject<?> getClassSubjectById(String classSubjectId);

}
