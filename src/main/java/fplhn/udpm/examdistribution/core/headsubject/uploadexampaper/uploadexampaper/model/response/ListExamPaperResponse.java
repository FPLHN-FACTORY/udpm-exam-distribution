package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface ListExamPaperResponse extends IsIdentify, HasOrderNumber {

    String getFileId();

    String getSubjectId();

    String getSubjectName();

    String getMajorName();

    String getExamPaperCode();

    String getStaffName();

    Long getCreatedDate();

    String getStatus();

    String getFacilityName();

    String getExamPaperType();

    String getMajorFacilityId();

}
