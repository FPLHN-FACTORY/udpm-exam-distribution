package fplhn.udpm.examdistribution.core.student.examshift.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;

public interface SStudentService {

    ResponseObject<?> findAllStudentByExamShiftCode(String examShiftCode);

    ResponseObject<?> findAllStudentRejoinByExamShiftCode(String examShiftCode);

    ResponseObject<?> kickStudentUnInstallExt(String examShiftCode);

}
