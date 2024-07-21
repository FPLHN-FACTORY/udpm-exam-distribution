package fplhn.udpm.examdistribution.infrastructure.config.email.service;

import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.THeadSubjectAndContentSendMailResponse;

public interface EmailService {

    void sendEmailPublicMockExamPaper(String[] listEmailBcc);

    void sendEmailWhenStartExamShift(THeadSubjectAndContentSendMailResponse tHeadSubjectAndContentSendMailResponse);

}
