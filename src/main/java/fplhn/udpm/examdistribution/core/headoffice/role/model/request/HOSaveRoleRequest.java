package fplhn.udpm.examdistribution.core.headoffice.role.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HOSaveRoleRequest {

    private String roleId;

    @NotBlank(message = "Role name cannot be blank")
    @Size(max = 100, message = "Role name must be less than 100 characters")
    @Pattern(regexp = "^[^\\s\\p{M}]+$", message = "Role name cannot contain whitespace or diacritical marks")
    private String roleName;

    @NotBlank(message = "Facility cannot be blank")
    private String idFacility;
}
