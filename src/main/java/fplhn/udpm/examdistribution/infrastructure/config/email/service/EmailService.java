package fplhn.udpm.examdistribution.infrastructure.config.email.service;

import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response.HDSendMailWhenCreateExamShiftResponse;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response.HSSendMailToHeadDepartmentWhenCreateExamShiftResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.THeadSubjectAndContentSendMailResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TSendMailToSupervisorWhenOpenExamPaperResponse;
import fplhn.udpm.examdistribution.infrastructure.config.email.modal.request.SendEmailPublicMockExamPaperRequest;

public interface EmailService {

    void sendEmailPublicMockExamPaper(SendEmailPublicMockExamPaperRequest listEmailBcc);

    void sendEmailWhenStartExamShift(THeadSubjectAndContentSendMailResponse tHeadSubjectAndContentSendMailResponse);

    void sendEmailWhenHeadDepartmentCreateExamShift(
            HDSendMailWhenCreateExamShiftResponse hdSendMailWhenCreateExamShiftResponse, String password);

    void sendEmailWhenHeadSubjectCreateExamShift(
            HSSendMailToHeadDepartmentWhenCreateExamShiftResponse hsSendMailWhenCreateExamShiftResponse, String password
    );

    void sendEmailToSupervisorWhenOpenExamPaper(
            TSendMailToSupervisorWhenOpenExamPaperResponse tSendMailToSupervisorWhenOpenExamPaperResponse, String password);

}
