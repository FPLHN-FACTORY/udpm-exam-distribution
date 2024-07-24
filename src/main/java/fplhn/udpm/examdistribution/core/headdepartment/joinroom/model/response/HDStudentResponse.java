package fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface HDStudentResponse extends IsIdentify {

    String getName();

    String getStudentCode();

    String getEmail();

    Long getJoinTime();

    String getPicture();

    Integer getIsViolation();

}
