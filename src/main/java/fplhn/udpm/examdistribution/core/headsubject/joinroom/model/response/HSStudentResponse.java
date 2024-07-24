package fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface HSStudentResponse extends IsIdentify {

    String getName();

    String getStudentCode();

    String getEmail();

    Long getJoinTime();

    String getPicture();

    Integer getIsViolation();

}
