package fplhn.udpm.examdistribution.core.headdepartment.joinroom.staff.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;

public interface HDStaffService {

    ResponseObject<?> findFirstSupervisorIdByExamShiftCode(String examShiftCode);

    ResponseObject<?> findSecondSupervisorIdByExamShiftCode(String examShiftCode);

}
