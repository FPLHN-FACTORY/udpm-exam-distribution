package fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface HDAllExamShiftResponse extends IsIdentify {

    String getExamShiftCode();

    String getRoom();

    String getCodeFirstSupervisor();

    String getNameFirstSupervisor();

    String getStatus();

}
