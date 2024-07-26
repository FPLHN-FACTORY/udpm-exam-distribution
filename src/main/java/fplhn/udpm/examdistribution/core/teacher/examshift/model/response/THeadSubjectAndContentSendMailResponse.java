package fplhn.udpm.examdistribution.core.teacher.examshift.model.response;

public interface THeadSubjectAndContentSendMailResponse {

    String getExamShiftCode();

    String getRoom();

    Long getExamDate();

    String getShift();

    String getClassSubjectCode();

    String getSubjectName();

    String getCodeFirstSupervisor();

    String getNameFirstSupervisor();

    String getCodeSecondSupervisor();

    String getNameSecondSupervisor();

    String getPathExamPaper();

    String getAccountFeHeadSubject();

    String getAccountFptHeadSubject();

}
