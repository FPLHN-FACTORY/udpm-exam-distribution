package fplhn.udpm.examdistribution.core.headdepartment.joinroom.student.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;

public interface HDStudentService {

    ResponseObject<?> findAllStudentByExamShiftCode(String examShiftCode);

}
