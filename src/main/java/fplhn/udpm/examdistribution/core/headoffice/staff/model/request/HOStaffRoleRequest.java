package fplhn.udpm.examdistribution.core.headoffice.staff.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HOStaffRoleRequest extends PageableRequest {

    private String staffId;

    private String roleName;

    private String idFacility;
}
