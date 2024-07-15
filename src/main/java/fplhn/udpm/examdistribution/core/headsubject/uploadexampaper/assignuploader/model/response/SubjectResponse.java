package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface SubjectResponse extends IsIdentify, HasOrderNumber {

    String getFileId();

    String getSubjectCode();

    String getSubjectName();

    String getSubjectType();

    String getDepartmentName();

    Long getCreatedDate();

}
