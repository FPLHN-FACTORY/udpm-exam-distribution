package fplhn.udpm.examdistribution.core.headdepartment.managehos.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface StaffResponse extends IsIdentify, HasOrderNumber {

    String getStaffCode();

    String getStaffName();

    String getAccountFPT();

    String getAccountFE();

}
