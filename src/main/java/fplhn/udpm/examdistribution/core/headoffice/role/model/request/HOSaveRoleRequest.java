package fplhn.udpm.examdistribution.core.headoffice.role.model.request;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "role name is empty")
    private String roleName;

    @NotBlank(message = "facility is not found")
    private String idFacility;
}
