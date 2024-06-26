package fplhn.udpm.examdistribution.core.headoffice.semester.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUpdateSemesterRequest {

    @NotBlank(message = "Tên học kỳ không được để trống")
    private String semesterName;

    @NotNull(message = "Năm học không được để trống")
    private Integer semesterYear;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private Long startTime;

}
