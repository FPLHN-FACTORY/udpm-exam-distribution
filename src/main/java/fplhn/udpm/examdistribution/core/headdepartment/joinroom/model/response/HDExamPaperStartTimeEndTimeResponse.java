package fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface HDExamPaperStartTimeEndTimeResponse extends IsIdentify {

    Long getStartTime();

    Long getEndTime();

}
