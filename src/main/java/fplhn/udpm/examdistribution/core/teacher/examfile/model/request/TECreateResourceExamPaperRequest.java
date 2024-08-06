package fplhn.udpm.examdistribution.core.teacher.examfile.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TECreateResourceExamPaperRequest {

    @NotBlank(message = "Tài nguyên không được để trống")
    private String resource;

    private String examPaperId;

}
