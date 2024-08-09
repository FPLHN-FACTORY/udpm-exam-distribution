package fplhn.udpm.examdistribution.core.headoffice.semester.model.response;

public interface SemesterResponse {

    String getSemesterId();

    String getSemesterName();

    Long getStartTime();

    Long getEndTime();

    Long getStartTimeBlock1();

    Long getEndTimeBlock1();

    Long getStartTimeBlock2();

    Long getEndTimeBlock2();
}
