package fplhn.udpm.examdistribution.core.student.examshift.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface SExamPaperStartTimeEndTimeResponse extends IsIdentify {

    Long getStartTime();

    Long getEndTime();

}
