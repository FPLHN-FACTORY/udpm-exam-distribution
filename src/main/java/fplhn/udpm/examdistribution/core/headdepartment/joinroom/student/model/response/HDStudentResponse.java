package fplhn.udpm.examdistribution.core.headdepartment.joinroom.student.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface HDStudentResponse extends IsIdentify {

    String getName();

    String getStudentCode();

    String getEmail();

    Long getJoinTime();
}
