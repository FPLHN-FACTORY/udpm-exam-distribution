package fplhn.udpm.examdistribution.core.teacher.classsubject.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.classsubject.model.request.ClassSubjectTeacherRequest;

public interface ClassSubjectTeacherService {

    ResponseObject<?> getClassSubjectIdByRequest(ClassSubjectTeacherRequest classSubjectTeacherRequest);

}
