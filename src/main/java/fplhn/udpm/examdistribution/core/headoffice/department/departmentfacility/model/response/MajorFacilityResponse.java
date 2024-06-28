package fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface MajorFacilityResponse extends IsIdentify, HasOrderNumber {

    String getMajorName();

    String getHeadMajorCodeName();

    FacilityDepartmentInfoResponse getFacilityDepartmentInfo();

}
