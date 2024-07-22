package fplhn.udpm.examdistribution.core.headoffice.facility.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface FacilityChildResponse extends IsIdentify, HasOrderNumber {

    String getFacilityChildCode();

    String getFacilityChildName();

    Integer getFacilityChildStatus();

    Long getCreatedDate();
}
