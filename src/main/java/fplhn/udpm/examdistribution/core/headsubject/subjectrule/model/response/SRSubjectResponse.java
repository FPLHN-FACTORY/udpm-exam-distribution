package fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface SRSubjectResponse extends IsIdentify, HasOrderNumber {

    String getSubjectCode();

    String getSubjectName();

    String getSubjectType();

    String getDepartmentName();

    Long getCreatedDate();

    String getFileId();

    Integer getAllowOnline();

}
