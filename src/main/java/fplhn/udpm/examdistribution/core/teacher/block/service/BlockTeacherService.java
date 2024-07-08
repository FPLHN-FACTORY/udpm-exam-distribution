package fplhn.udpm.examdistribution.core.teacher.block.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;

public interface BlockTeacherService {

    ResponseObject<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId);

    ResponseObject<?> findBlockId(String classSubjectCode, String subjectId, Long examShiftDate);

}
