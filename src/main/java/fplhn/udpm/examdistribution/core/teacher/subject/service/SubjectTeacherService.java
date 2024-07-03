package fplhn.udpm.examdistribution.core.teacher.subject.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;

public interface SubjectTeacherService {

    ResponseObject<?> findAllByClassSubjectCode(String classSubjectCode);

}
