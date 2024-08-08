package fplhn.udpm.examdistribution.infrastructure.config.job.examshift.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImportShiftRequest {

    private int orderNumber;

    private String buildingLetter;

    private String startDate;

    private String shift;

    private String roomInfo;

    private String subjectName;

    private String subjectCode;

    private String lastTimeLearnOfSubject;

    private String classSubjectCode;

    private String teacherOfSubject;

    private String firstSupervisorCode;

    private String secondSupervisorCode;

    private String majorCode;

    private String duplicate;

    private String totalStudent;

    private String block;

    private String note;

    private String campusCode;

}
