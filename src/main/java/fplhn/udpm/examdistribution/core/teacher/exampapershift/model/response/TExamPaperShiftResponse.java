package fplhn.udpm.examdistribution.core.teacher.exampapershift.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface TExamPaperShiftResponse extends IsIdentify {

    String getExamShiftId();

    String getExamPaperId();

}
