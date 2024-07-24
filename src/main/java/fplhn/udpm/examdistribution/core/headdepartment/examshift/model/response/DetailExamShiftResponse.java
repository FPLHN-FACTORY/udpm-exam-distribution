package fplhn.udpm.examdistribution.core.headdepartment.examshift.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface DetailExamShiftResponse extends IsIdentify {

    String getClassSubjectCode();

    String getSubjectInfo();

    String getStaffConductCode();

    String getFirstSupervisorCode();

    String getSecondSupervisorCode();

    String getJoinCode();

    String getRoom();

    Long getExamDate();

    String getShift();

}
