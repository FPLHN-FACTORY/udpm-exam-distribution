package fplhn.udpm.examdistribution.core.teacher.examshift.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface TExamShiftResponse extends IsIdentify {

    String getExamShiftCode();

    String getPathExamRule();

}
