package fplhn.udpm.examdistribution.core.headsubject.joinroom.staff.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;

public interface HSStaffService {

    ResponseObject<?> findFirstSupervisorIdByExamShiftCode(String examShiftCode);

    ResponseObject<?> findSecondSupervisorIdByExamShiftCode(String examShiftCode);

}
