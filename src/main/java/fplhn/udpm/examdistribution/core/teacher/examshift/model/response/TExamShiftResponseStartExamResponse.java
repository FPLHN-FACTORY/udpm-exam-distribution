package fplhn.udpm.examdistribution.core.teacher.examshift.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface TExamShiftResponseStartExamResponse extends IsIdentify {

    String getExamShiftCode();

    String getClassSubjectId();

    String getSubjectId();

    String getBlockId();

    String getDepartmentFacilityId();

}
