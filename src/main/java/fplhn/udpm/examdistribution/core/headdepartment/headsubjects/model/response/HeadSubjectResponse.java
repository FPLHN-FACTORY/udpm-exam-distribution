package fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface HeadSubjectResponse extends IsIdentify, HasOrderNumber {

    String getStaffCode();

    String getStaffName();

    String getEmailFPT();

    String getEmailFE();

    Short getIsAssigned();

}
