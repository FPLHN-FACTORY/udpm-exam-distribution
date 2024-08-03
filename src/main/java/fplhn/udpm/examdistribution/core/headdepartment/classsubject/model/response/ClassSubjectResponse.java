package fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface ClassSubjectResponse extends IsIdentify, HasOrderNumber {

    String getClassSubjectCode();

    String getSubjectCode();

    String getSubjectName();

    String getShift();

    Long getDay();

    String getStaffCode();

    String getStaffName();

    String getBlockId();

    String getBlockName();

    String getFacilityChildId();

    String getFacilityChildName();

    String getSemesterName();

    Integer getYear();

}
