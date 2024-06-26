package fplhn.udpm.examdistribution.core.headoffice.department.department.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;

public interface MajorResponse extends HasOrderNumber {

    String getMajorId();

    String getMajorName();

    Long getMajorStatus();

    Long getCreatedDate();

}
