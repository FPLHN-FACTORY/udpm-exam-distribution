package fplhn.udpm.examdistribution.core.headsubject.joinroom.student.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;

public interface HSStudentService {

    ResponseObject<?> findAllStudentByExamShiftCode(String examShiftCode);

}
