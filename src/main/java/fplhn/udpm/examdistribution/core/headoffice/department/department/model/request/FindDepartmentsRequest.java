package fplhn.udpm.examdistribution.core.headoffice.department.department.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FindDepartmentsRequest extends PageableRequest {

    private String departmentName;

}
