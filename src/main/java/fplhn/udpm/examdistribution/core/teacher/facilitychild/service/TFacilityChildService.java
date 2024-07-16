package fplhn.udpm.examdistribution.core.teacher.facilitychild.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;

public interface TFacilityChildService {

    ResponseObject<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId);

}
