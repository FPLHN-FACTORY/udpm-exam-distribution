package fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface HSExamPaperShiftInfoAndPathResponse extends IsIdentify {

    String getPath();

    Long getStartTime();

    Long getEndTime();

}
