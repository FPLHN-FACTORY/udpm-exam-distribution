package fplhn.udpm.examdistribution.core.headoffice.subject.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUpdateSubjectRequest {

    @NotEmpty(message = "Mã môn học không được để trống")
    @NotNull(message = "Mã môn học không được để trống")
    private String subjectCode;

    @NotEmpty(message = "Tên môn học không được để trống")
    @NotNull(message = "Tên môn học không được để trống")
    private String subjectName;

    @NotNull(message = "Tên bộ môn không được để trống")
    @NotEmpty(message = "Tên bộ môn không được để trống")
    private String departmentId;

    @NotEmpty(message = "Hình thức không được để trống")
    @NotNull(message = "Hình thức không được để trống")
    private String subjectType;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private Long startDate;

}
