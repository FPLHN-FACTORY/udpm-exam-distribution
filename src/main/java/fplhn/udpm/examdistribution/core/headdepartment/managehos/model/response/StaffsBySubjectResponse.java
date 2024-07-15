package fplhn.udpm.examdistribution.core.headdepartment.managehos.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface StaffsBySubjectResponse extends IsIdentify {

    Integer getIsHeadOfSubject();

    String getStaffCode();

    String getStaffName();

    String getAccountFPT();

    String getAccountFE();

    String getSemesterInfo();

}
