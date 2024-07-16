package fplhn.udpm.examdistribution.core.student.student.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface SStudentResponse extends IsIdentify {

    String getName();

    String getStudentCode();

    String getEmail();

    Long getJoinTime();
}
