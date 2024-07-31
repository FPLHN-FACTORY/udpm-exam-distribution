package fplhn.udpm.examdistribution.infrastructure.config.job.prenotifyexam.response;

import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ExamShiftCommingInfoResponse {

    private String examShiftCode;

    private String room;

    private Long examDate;

    private String shift;

    private String classSubjectCode;

    private String subjectName;

    private String codeFirstSupervisor;

    private String nameFirstSupervisor;

    private String accountFeFirstSupervisor;

    private String accountFptFirstSupervisor;

    private String codeSecondSupervisor;

    private String nameSecondSupervisor;

    private String accountFeSecondSupervisor;

    private String accountFptSecondSupervisor;

    private String departmentFacilityId;

    private String headDepartmentEmail;

    private String headSubjectEmail;

    private String password;

    public ExamShiftCommingInfoResponse(
            String examShiftCode,
            String room,
            Long examDate,
            Shift shift,
            String classSubjectCode,
            String subjectName,
            String codeFirstSupervisor,
            String nameFirstSupervisor,
            String accountFeFirstSupervisor,
            String accountFptFirstSupervisor,
            String codeSecondSupervisor,
            String nameSecondSupervisor,
            String accountFeSecondSupervisor,
            String accountFptSecondSupervisor,
            String departmentFacilityId,
            String headDepartmentEmail,
            String headSubjectEmail
    ) {
        this.examShiftCode = examShiftCode;
        this.room = room;
        this.examDate = examDate;
        this.shift = Shift.getShift(shift);
        this.classSubjectCode = classSubjectCode;
        this.subjectName = subjectName;
        this.codeFirstSupervisor = codeFirstSupervisor;
        this.nameFirstSupervisor = nameFirstSupervisor;
        this.accountFeFirstSupervisor = accountFeFirstSupervisor;
        this.accountFptFirstSupervisor = accountFptFirstSupervisor;
        this.codeSecondSupervisor = codeSecondSupervisor;
        this.nameSecondSupervisor = nameSecondSupervisor;
        this.accountFeSecondSupervisor = accountFeSecondSupervisor;
        this.accountFptSecondSupervisor = accountFptSecondSupervisor;
        this.departmentFacilityId = departmentFacilityId;
        this.headDepartmentEmail = headDepartmentEmail;
        this.headSubjectEmail = headSubjectEmail;
    }

    public ExamShiftCommingInfoResponse() {
    }

}

