package fplhn.udpm.examdistribution.core.headoffice.facility.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityRequest extends PageableRequest {

    private String name;

    private String status;

}
