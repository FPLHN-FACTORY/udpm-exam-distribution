package fplhn.udpm.examdistribution.core.headoffice.block.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUpdateBlockRequest {

    @NotBlank(message = "Tên block không được để trống")
    private String blockName;

    private String semesterId;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private Long startTime;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private Long endTime;

}
