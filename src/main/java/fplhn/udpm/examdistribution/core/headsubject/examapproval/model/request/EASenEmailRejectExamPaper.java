package fplhn.udpm.examdistribution.core.headsubject.examapproval.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EASenEmailRejectExamPaper {

    private String sendToEmailAddress;

    private String examPaperCode;

    private Date timeReject;

    private String subjectName;

    private String majorName;

    private String departmentName;

}
