package fplhn.udpm.examdistribution.core.teacher.examfile.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface TSubjectResponse extends IsIdentify, HasOrderNumber {

    String getSubjectCode();

    String getSubjectName();

    String getSubjectType();

    String getDepartmentName();

    Long getCreatedDate();

}
