package fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignSubjectForHeadSubjectRequest {

    @NotNull(message = "SubjectId is required")
    private String subjectId;

}
