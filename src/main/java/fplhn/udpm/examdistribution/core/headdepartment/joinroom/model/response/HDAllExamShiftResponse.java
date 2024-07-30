package fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface HDAllExamShiftResponse extends IsIdentify {

    String getExamShiftCode();

    String getShift();

    String getRoom();

    String getSubjectName();

    String getCodeFirstSupervisor();

    String getNameFirstSupervisor();

    String getCodeSecondSupervisor();

    String getNameSecondSupervisor();

    String getStatus();

}
