package fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface CEPListExamPaperResponse extends IsIdentify, HasOrderNumber {

    String getFileId();

    String getSubjectId();

    String getSubjectName();

    String getMajorName();

    String getExamPaperCode();

    String getStaffName();

    Long getCreatedDate();

    String getStatus();

    Boolean getIsPublic();

    String getFacilityName();

    String getExamPaperType();

    String getMajorFacilityId();

    Integer getIsChoose();

    Integer getIsUpdateFile();

}
