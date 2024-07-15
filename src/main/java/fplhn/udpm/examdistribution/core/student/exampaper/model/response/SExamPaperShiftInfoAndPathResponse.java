package fplhn.udpm.examdistribution.core.student.exampaper.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface SExamPaperShiftInfoAndPathResponse extends IsIdentify {

    String getPath();

    Long getStartTime();

    Long getEndTime();

}
