package fplhn.udpm.examdistribution.core.headoffice.staff.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class HOStaffRoleChangePermissionRequest {

    @NotBlank(message = "Id staff cannot be blank")
    @Length(max = 255, message = "Id staff must be less than 255 characters")
    @Pattern(regexp = "^[^\\s]+$", message = "Id staff cannot contain whitespace")
    String idStaff;

    @NotBlank(message = "Id role cannot be blank")
    @Length(max = 255, message = "Id role must be less than 255 characters")
    @Pattern(regexp = "^[^\\s]+$", message = "Id role code cannot contain whitespace")
    String idRole;

}
