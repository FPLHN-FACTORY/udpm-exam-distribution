package fplhn.udpm.examdistribution.core.headoffice.role.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HORoleRequest extends PageableRequest {

    private String roleName;

    private String idFacility;
}
