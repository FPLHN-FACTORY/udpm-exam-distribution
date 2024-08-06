package fplhn.udpm.examdistribution.core.common.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class StaffSearchRequest {

    private String q;

    private String currentDepartmentFacilityId;

    private String currentUserId;

}
