package fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReassignHeadOfSubjectRequest {

    @NotNull(message = "ID môn học không được để trống")
    private String subjectId;

    @NotNull(message = "ID giáo viên không được để trống")
    private String staffId;

}