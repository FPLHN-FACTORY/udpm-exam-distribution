package fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface CEPListSemesterResponse extends IsIdentify {

    String getName();

    Long getStartTime();

    Long getEndTime();

}
