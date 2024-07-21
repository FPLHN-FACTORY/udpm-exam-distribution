package fplhn.udpm.examdistribution.core.headoffice.role.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HOSaveRoleRequest {

    private String roleId;

    @NotBlank(message = "Role name cannot be blank")
    @Size(max = 100, message = "Role name must be less than 100 characters")
    private String roleName;

    @NotBlank(message = "Facility cannot be blank")
    private String idFacility;
}
