package fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface TSubjectMockExamResponse extends IsIdentify, HasOrderNumber {

    String getSubjectName();

    String getId();

    String getDepartmentName();

    String getSubjectType();

    String getSemesterName();

    String getBlockName();

    Boolean getIsCurrentBlock();

    Boolean getIsExistsPracticeRoom();

    String getPracticeRoomId();
}
