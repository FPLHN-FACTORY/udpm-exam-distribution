package fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CEPUpdateResourceExamPaperRequest {

    @NotBlank(message = "Tài nguyên không được để trống")
    private String resource;

    private String resourceExamPaperId;

}
