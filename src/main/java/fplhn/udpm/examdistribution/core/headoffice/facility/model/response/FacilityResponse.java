package fplhn.udpm.examdistribution.core.headoffice.facility.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;
import org.springframework.beans.factory.annotation.Value;

public interface FacilityResponse extends IsIdentify, HasOrderNumber {

    String getFacilityName();

    String getFacilityCode();

    Integer getFacilityStatus();

    Long getCreatedDate();

}
