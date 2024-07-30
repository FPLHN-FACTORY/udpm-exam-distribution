package fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response;

public interface HSSendMailToSupervisorWhenCreateExamShiftResponse {

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

}
