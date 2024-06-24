package fplhn.udpm.examdistribution.core.headoffice.department.department.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface DetailDepartmentResponse extends IsIdentify {

    String getSubjectCode();

    String getSubjectName();

    String getDepartmentId();

    String getSubjectType();

    String getSubjectStatus();

    Long getCreatedDate();

}
