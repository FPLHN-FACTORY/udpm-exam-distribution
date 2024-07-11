package fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface HSAllExamShiftResponse extends IsIdentify {

    String getExamShiftCode();

    String getRoom();

    String getCodeFirstSupervisor();

    String getNameFirstSupervisor();

    String getStatus();

}
