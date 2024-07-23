package fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface HDExamPaperShiftInfoAndPathResponse extends IsIdentify {

    String getPath();

    Long getStartTime();

    Long getEndTime();

}
