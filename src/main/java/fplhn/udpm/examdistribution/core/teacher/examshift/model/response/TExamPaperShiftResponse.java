package fplhn.udpm.examdistribution.core.teacher.examshift.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface TExamPaperShiftResponse extends IsIdentify {

    String getExamShiftId();

    String getExamPaperId();

    String getPassword();

}
