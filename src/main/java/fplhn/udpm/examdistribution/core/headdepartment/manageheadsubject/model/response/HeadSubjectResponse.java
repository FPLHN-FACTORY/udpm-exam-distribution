package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface HeadSubjectResponse extends IsIdentify, HasOrderNumber {

    String getStaffCode();

    String getStaffName();

    String getAccountFPT();

    String getAccountFE();

    String getRoleName();

    String getSemesterInfo();

}