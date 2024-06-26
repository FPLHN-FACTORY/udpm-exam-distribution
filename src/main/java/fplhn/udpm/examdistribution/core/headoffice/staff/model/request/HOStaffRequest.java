package fplhn.udpm.examdistribution.core.headoffice.staff.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HOStaffRequest extends PageableRequest {

    private String roleId;

    private String accountFptOrFe;

    private String staffCodeOrName;

    private String departmentFacilityId;

    private String semesterId;

}
