package fplhn.udpm.examdistribution.core.headdepartment.managehos.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface SubjectBySubjectGroupResponse extends IsIdentify, HasOrderNumber {

    String getSubjectCode();

    String getSubjectName();

    String getSubjectType();

    String getIsSubjectInGroup();

}
