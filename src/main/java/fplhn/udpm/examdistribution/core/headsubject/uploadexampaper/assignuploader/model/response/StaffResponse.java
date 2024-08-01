package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface StaffResponse extends IsIdentify, HasOrderNumber {

    String getName();

    String getStaffCode();

    String getAccountFe();

    String getAccountFpt();

    Long getCreatedDate();

    Integer getIsAssigned();

    Integer getMaxUpload();

    Integer getIsHasSampleExamPaper();

    Integer haveTaught();

}
