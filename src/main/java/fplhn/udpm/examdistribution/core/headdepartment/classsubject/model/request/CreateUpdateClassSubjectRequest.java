package fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUpdateClassSubjectRequest {

    @NotBlank(message = "Mã lớp môn không được để trống")
    private String classSubjectCode;


    @NotNull(message = "Ngày không được để trống")
    private Long day;

    @NotBlank(message = "Ca không được để trống")
    private String shift;


    @NotBlank(message = "Mã môn không được để trống")
    private String subjectCode;

    @NotBlank(message = "Block không được để trống")
    private String blockId;

    @NotBlank(message = "Cơ sở không được để trống")
    private String facilityChildId;

    @NotBlank(message = "Mã nhân viên không được để trống")
    private String staffCode;

}
