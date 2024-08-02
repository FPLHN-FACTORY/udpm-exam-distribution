package fplhn.udpm.examdistribution.core.student.examshift.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface SExamShiftResponse extends IsIdentify {

    String getExamShiftCode();

    String getSubjectName();

    String getClassSubjectCode();

    String getPathExamRule();

}
