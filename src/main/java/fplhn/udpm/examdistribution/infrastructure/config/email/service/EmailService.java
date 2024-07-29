package fplhn.udpm.examdistribution.infrastructure.config.email.service;

import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response.HDSendMailWhenCreateExamShiftResponse;
import fplhn.udpm.examdistribution.core.headsubject.examapproval.model.request.EASenEmailRejectExamPaper;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response.HSSendMailToHeadDepartmentWhenCreateExamShiftResponse;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response.HSSendMailToSupervisorWhenCreateExamShiftResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.THeadSubjectAndContentSendMailResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TSendMailToSupervisorWhenOpenExamPaperResponse;
import fplhn.udpm.examdistribution.infrastructure.config.email.modal.request.SendEmailPublicMockExamPaperRequest;

import java.util.List;

public interface EmailService {

    void sendEmailPublicMockExamPaper(SendEmailPublicMockExamPaperRequest listEmailBcc);

    void sendEmailWhenStartExamShift(THeadSubjectAndContentSendMailResponse tHeadSubjectAndContentSendMailResponse);

    void sendEmailWhenHeadDepartmentCreateExamShift(
            HDSendMailWhenCreateExamShiftResponse hdSendMailWhenCreateExamShiftResponse, String password);

    void sendEmailWhenHeadSubjectCreateExamShift(
            HSSendMailToSupervisorWhenCreateExamShiftResponse hsSendMailToSupervisorWhenCreateExamShiftResponse, String password
    );

    void sendEmailToHeadDepartmentWhenCreateExamShift(
            List<HSSendMailToHeadDepartmentWhenCreateExamShiftResponse> hsSendMailToHeadDepartmentWhenCreateExamShiftResponses, String password, String accountFptHeadDepartment);

    void sendEmailToSupervisorWhenOpenExamPaper(
            TSendMailToSupervisorWhenOpenExamPaperResponse tSendMailToSupervisorWhenOpenExamPaperResponse, String password);

    void sendEmailWhenRejectExamPaper(EASenEmailRejectExamPaper request);
}
