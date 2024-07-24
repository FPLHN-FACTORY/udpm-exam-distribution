package fplhn.udpm.examdistribution.core.headsubject.joinroom.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;

public interface HSBlockService {

    ResponseObject<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId);

    ResponseObject<?> findBlockId(String classSubjectCode, String subjectId, Long examShiftDate);

}
