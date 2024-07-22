package fplhn.udpm.examdistribution.core.headdepartment.examshift.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface ExamShiftResponse extends IsIdentify, HasOrderNumber {

    String getClassSubjectCode();

    String getSubjectInfo();

    String getFirstSupervisor();

    String getSecondSupervisor();

    String getJoinCode();

    String getRoom();

    Long getExamDate();

    String getShift();

    Short getIsCanEdit();

}
