package fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class HeadSubjectSearchRequest {

    private String q;

    private String currentSemesterId;

    private String currentDepartmentFacilityId;

    private String currentUserId;

    private String currentFacilityId;

}
