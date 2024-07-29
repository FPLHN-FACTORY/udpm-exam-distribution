package fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response;

public interface HDSendMailWhenCreateExamShiftResponse {

    String getExamShiftCode();

    String getRoom();

    Long getExamDate();

    String getShift();

    String getClassSubjectCode();

    String getSubjectName();

    String getCodeFirstSupervisor();

    String getNameFirstSupervisor();

    String getAccountFeFirstSupervisor();

    String getAccountFptFirstSupervisor();

    String getCodeSecondSupervisor();

    String getNameSecondSupervisor();

    String getAccountFeSecondSupervisor();

    String getAccountFptSecondSupervisor();

    String getAccountFeHeadSubject();

    String getAccountFptHeadSubject();

}
