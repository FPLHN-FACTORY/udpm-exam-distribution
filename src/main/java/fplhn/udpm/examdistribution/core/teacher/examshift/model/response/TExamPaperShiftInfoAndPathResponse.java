package fplhn.udpm.examdistribution.core.teacher.examshift.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface TExamPaperShiftInfoAndPathResponse extends IsIdentify {

    String getPath();

    Long getStartTime();

    Long getEndTime();

}
