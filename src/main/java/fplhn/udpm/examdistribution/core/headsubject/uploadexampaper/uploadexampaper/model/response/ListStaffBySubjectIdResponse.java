package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface ListStaffBySubjectIdResponse extends HasOrderNumber, IsIdentify {

    String getCode();

    String getName();

    String getEmailFpt();

    String getEmailFe();

    Integer getIsPublic();

}
