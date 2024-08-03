package fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReassignHeadSubjectRequest {

    @NotNull(message = "Current head subject id is required")
    private String currentHeadSubjectId;

    @NotNull(message = "New head subject id is required")
    private String newHeadSubjectId;

}
