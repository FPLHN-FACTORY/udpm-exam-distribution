package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface UEPListExamPaperResponse extends IsIdentify, HasOrderNumber {

    String getFileId();

    String getSubjectId();

    String getSubjectName();

    String getMajorName();

    String getExamPaperCode();

    String getExamPaperId();

    String getStaffName();

    Long getCreatedDate();

    String getStatus();

    Boolean getIsPublic();

    String getFacilityName();

    String getExamPaperType();

    String getMajorFacilityId();

    String getTotalUsed();

}
