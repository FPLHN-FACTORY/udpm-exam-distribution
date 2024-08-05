package fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface HDExamShiftResponse extends IsIdentify {

    String getExamShiftCode();

    String getSubjectName();

    String getClassSubjectCode();

    String getPassword();

    String getPathExamRule();

}
