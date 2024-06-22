package fplhn.udpm.examdistribution.core.headoffice.subject.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface DetailSubjectResponse extends IsIdentify {

    String getSubjectCode();

    String getSubjectName();

    String getDepartmentId();

    String getSubjectType();

    String getSubjectStatus();

    Long getCreatedDate();

}
