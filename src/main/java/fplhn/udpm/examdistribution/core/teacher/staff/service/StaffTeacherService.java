package fplhn.udpm.examdistribution.core.teacher.staff.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;

public interface StaffTeacherService {

    ResponseObject<?> findFirstSupervisorIdByExamShiftCode(String examShiftCode);

    ResponseObject<?> findSecondSupervisorIdByExamShiftCode(String examShiftCode);

}