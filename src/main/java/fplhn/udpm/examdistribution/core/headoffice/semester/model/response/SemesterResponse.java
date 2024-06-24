package fplhn.udpm.examdistribution.core.headoffice.semester.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface SemesterResponse extends IsIdentify, HasOrderNumber {

    String getSemesterName();

    Integer getSemesterYear();

    Long getStartTime();

    Integer getSemesterStatus();

}
