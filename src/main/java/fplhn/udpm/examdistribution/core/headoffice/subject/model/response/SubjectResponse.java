package fplhn.udpm.examdistribution.core.headoffice.subject.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface SubjectResponse extends IsIdentify, HasOrderNumber {

    String getSubjectCode();

    String getSubjectName();

    String getDepartmentName();

    String getSubjectType();

    String getSubjectStatus();

    Long getCreatedDate();

}
