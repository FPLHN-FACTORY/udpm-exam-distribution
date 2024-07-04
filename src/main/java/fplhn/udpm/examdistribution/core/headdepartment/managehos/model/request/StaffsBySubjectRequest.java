package fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffsBySubjectRequest extends StaffRequest {

    @NotNull(message = "ID môn học không được để trống")
    private String subjectId;

}
