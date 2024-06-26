package fplhn.udpm.examdistribution.core.headoffice.facility.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface FacilityResponse extends IsIdentify, HasOrderNumber {

    String getFacilityName();

    String getFacilityStatus();

}
