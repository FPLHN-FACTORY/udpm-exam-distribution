package fplhn.udpm.examdistribution.core.teacher.examshift.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;

public interface TBlockService {

    ResponseObject<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId);

    ResponseObject<?> findBlockId(String classSubjectCode, String subjectId, Long examShiftDate);

}
