package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface SubjectGroupAssignedResponse extends IsIdentify, HasOrderNumber {

    String getAttachRoleName();

    Long getAssigned();

}
