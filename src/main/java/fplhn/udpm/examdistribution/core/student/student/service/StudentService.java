package fplhn.udpm.examdistribution.core.student.student.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;

public interface StudentService {

    ResponseObject<?> findAllStudentByExamShiftCode(String examShiftCode);

    ResponseObject<?> findAllStudentRejoinByExamShiftCode(String examShiftCode);

}
