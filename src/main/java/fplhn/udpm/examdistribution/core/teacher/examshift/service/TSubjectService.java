package fplhn.udpm.examdistribution.core.teacher.examshift.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;

public interface TSubjectService {

    ResponseObject<?> findAllByClassSubjectCode(String classSubjectCode);

}