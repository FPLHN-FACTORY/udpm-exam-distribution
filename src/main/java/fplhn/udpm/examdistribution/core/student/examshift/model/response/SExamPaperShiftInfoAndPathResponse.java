package fplhn.udpm.examdistribution.core.student.examshift.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface SExamPaperShiftInfoAndPathResponse extends IsIdentify {

    String getPath();

    Long getStartTime();

    Long getEndTime();

    Boolean getAllowOnline();

    Integer getExamShiftStatus();

    String getPassword();

}
