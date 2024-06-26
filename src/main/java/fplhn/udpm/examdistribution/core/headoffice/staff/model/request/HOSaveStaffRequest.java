package fplhn.udpm.examdistribution.core.headoffice.staff.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HOSaveStaffRequest {

    private String id;

    @NotBlank(message = "Name cannot be blank")
    @Length(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @NotBlank(message = "Staff code cannot be blank")
    @Length(max = 50, message = "Staff code must be less than 50 characters")
    @Pattern(regexp = "^[^\\s]+$", message = "Staff code cannot contain whitespace")
    private String staffCode;

    @NotBlank(message = "Account FE cannot be blank")
    @Length(max = 100, message = "Account FE must be less than 100 characters")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@fe\\.edu\\.vn$", message = "Must be a valid email ending with @fe.edu.vn")
    private String accountFe;

    @NotBlank(message = "Account FPT cannot be blank")
    @Length(max = 100, message = "Account FPT must be less than 100 characters")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@fpt\\.edu\\.vn$", message = "Must be a valid email ending with @fpt.edu.vn")
    private String accountFpt;

    @NotBlank(message = "Department Facility ID cannot be blank")
    @Length(max = 100, message = "Department Facility ID must be less than 100 characters")
    private String departmentFacilityId;

}
