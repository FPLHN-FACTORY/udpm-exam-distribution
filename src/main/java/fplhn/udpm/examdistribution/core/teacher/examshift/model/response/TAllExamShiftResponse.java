package fplhn.udpm.examdistribution.core.teacher.examshift.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface TAllExamShiftResponse extends IsIdentify {

    String getExamShiftCode();

    String getShift();

    String getRoom();

    String getClassSubjectCode();

    String getSubjectName();

    String getCodeFirstSupervisor();

    String getNameFirstSupervisor();

    String getCodeSecondSupervisor();

    String getNameSecondSupervisor();

    String getStatus();

}
