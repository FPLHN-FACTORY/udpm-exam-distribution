package fplhn.udpm.examdistribution.core.headdepartment.joinroom.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;

public interface HDBlockService {

    ResponseObject<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId);

    ResponseObject<?> findBlockId(String classSubjectCode, String subjectId, Long examShiftDate);

}
