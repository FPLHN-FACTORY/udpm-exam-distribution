package fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface SubjectAssignResponse extends IsIdentify, HasOrderNumber {

    String getSubjectCode();

    String getSubjectName();

    String getSubjectType();

    Short getIsAssigned();

}
