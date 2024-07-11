package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface ListSemesterResponse extends IsIdentify {

    String getName();

    Long getStartTime();

    Long getEndTime();

}
