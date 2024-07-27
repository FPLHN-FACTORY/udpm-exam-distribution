package fplhn.udpm.examdistribution.infrastructure.config.email.modal.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class SendEmailPublicMockExamPaperRequest {

    private String[] listEmailBcc;

    private String examPaperCode;

    private Date timeSend;

    private String subjectName;

    private String majorName;

    private String departmentName;

    private String semesterName;

}
