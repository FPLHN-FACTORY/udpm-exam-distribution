package fplhn.udpm.examdistribution.core.headdepartment.joinroom.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;

public interface HDStaffService {

    ResponseObject<?> findFirstSupervisorIdByExamShiftCode(String examShiftCode);

    ResponseObject<?> findSecondSupervisorIdByExamShiftCode(String examShiftCode);

}
