package fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.response;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MajorFacilitiesResponse {

    PageableObject<?> majorFacilities;

    FacilityDepartmentInfoResponse facilityDepartmentInfo;

}
