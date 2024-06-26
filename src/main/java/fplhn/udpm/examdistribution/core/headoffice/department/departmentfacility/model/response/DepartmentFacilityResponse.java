package fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;

public interface DepartmentFacilityResponse extends HasOrderNumber {

    String getDepartmentFacilityId();

    String getFacilityId();

    String getHeadOfDepartmentId();

    String getFacilityName();

    String getHeadOfDepartmentName();

    String getHeadOfDepartmentCode();

    Long getDepartmentFacilityStatus();

    Long getCreatedDate();

}
