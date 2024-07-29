package fplhn.udpm.examdistribution.core.teacher.examshift.model.response;

public interface TSendMailToSupervisorWhenOpenExamPaperResponse {

    String getExamShiftCode();

    String getRoom();

    Long getExamDate();

    String getShift();

    String getClassSubjectCode();

    String getSubjectName();

    String getAccountFeFirstSupervisor();

    String getAccountFptFirstSupervisor();

    String getAccountFeSecondSupervisor();

    String getAccountFptSecondSupervisor();

    String getPathExamPaper();

}
