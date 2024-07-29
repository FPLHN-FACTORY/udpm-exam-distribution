package fplhn.udpm.examdistribution.infrastructure.config.email.service;

import fplhn.udpm.examdistribution.core.headsubject.examapproval.model.request.EASenEmailRejectExamPaper;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.THeadSubjectAndContentSendMailResponse;
import fplhn.udpm.examdistribution.infrastructure.config.email.modal.request.SendEmailPublicMockExamPaperRequest;

public interface EmailService {

    void sendEmailPublicMockExamPaper(SendEmailPublicMockExamPaperRequest listEmailBcc);

    void sendEmailWhenStartExamShift(THeadSubjectAndContentSendMailResponse tHeadSubjectAndContentSendMailResponse);

    void sendEmailWhenRejectExamPaper(EASenEmailRejectExamPaper request);

}
