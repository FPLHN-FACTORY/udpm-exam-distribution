package fplhn.udpm.examdistribution.core.teacher.trackhistory.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;

public interface ListStudentMakeMistakeResponse extends HasOrderNumber {

    String getUrl();

    Long getTimeViolation();

    Long getCreatedDate();

}
