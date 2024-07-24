package fplhn.udpm.examdistribution.core.teacher.examshift.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;

public interface TStaffService {

    ResponseObject<?> findFirstSupervisorIdByExamShiftCode(String examShiftCode);

    ResponseObject<?> findSecondSupervisorIdByExamShiftCode(String examShiftCode);

}
