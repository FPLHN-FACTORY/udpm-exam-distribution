package fplhn.udpm.examdistribution.infrastructure.config.email.service.impl;

import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response.HDSendMailWhenCreateExamShiftResponse;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response.HSSendMailToHeadDepartmentWhenCreateExamShiftResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.THeadSubjectAndContentSendMailResponse;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveManagerService;
import fplhn.udpm.examdistribution.infrastructure.config.drive.utils.PermissionDetail;
import fplhn.udpm.examdistribution.infrastructure.config.email.modal.request.SendEmailPublicMockExamPaperRequest;
import fplhn.udpm.examdistribution.infrastructure.config.email.service.EmailService;
import fplhn.udpm.examdistribution.infrastructure.constant.MailConstant;
import fplhn.udpm.examdistribution.utils.DateTimeUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private final GoogleDriveManagerService googleDriveManagerService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendEmailPublicMockExamPaper(SendEmailPublicMockExamPaperRequest request) {

        String toEmail = "nguyenvimanhnqt@gmail.com";

        String header = MailConstant.HEADER
                .replace("${title}", "Thông báo đề thi thử đã được mở.");

        String body = MailConstant.BODY
                .replace("${examPaperCode}", request.getExamPaperCode())
                .replace("${timeSend}", DateTimeUtil.parseLongToString(request.getTimeSend().getTime()))
                .replace("${subjectName}", request.getSubjectName())
                .replace("${majorName}", request.getMajorName())
                .replace("${departmentName}", request.getDepartmentName())
                .replace("${semesterName}", request.getSemesterName());

        String footer = MailConstant.FOOTER;


        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = null;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.toString());
            ClassPathResource resource = new ClassPathResource(MailConstant.LOGO_PATH);
            mimeMessageHelper.setFrom("nguyenvimanhnqt@gmail.com");
            mimeMessageHelper.setTo(toEmail);
            mimeMessageHelper.setBcc(request.getListEmailBcc());
            mimeMessageHelper.setText(header + body + footer, true);
            mimeMessageHelper.setSubject(MailConstant.SUBJECT);
            mimeMessageHelper.addInline("logoImage", resource);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendEmailWhenStartExamShift(THeadSubjectAndContentSendMailResponse tHeadSubjectAndContentSendMailResponse) {

        String toEmail = tHeadSubjectAndContentSendMailResponse.getAccountFptHeadSubject();

        String header = MailConstant.HEADER
                .replace("${title}", "Thông báo ca thi đã bắt đầu");

        String body = MailConstant.BODY_START_EXAM_SHIFT
                .replace("${examShiftCode}", tHeadSubjectAndContentSendMailResponse.getExamShiftCode())
                .replace("${examDate}", DateTimeUtil.parseLongToString(tHeadSubjectAndContentSendMailResponse.getExamDate()))
                .replace("${room}", tHeadSubjectAndContentSendMailResponse.getRoom())
                .replace("${shift}", tHeadSubjectAndContentSendMailResponse.getShift())
                .replace("${classSubjectCode}", tHeadSubjectAndContentSendMailResponse.getClassSubjectCode())
                .replace("${subjectName}", tHeadSubjectAndContentSendMailResponse.getSubjectName())
                .replace("${nameFirstSupervisor}", tHeadSubjectAndContentSendMailResponse.getNameFirstSupervisor())
                .replace("${codeFirstSupervisor}", tHeadSubjectAndContentSendMailResponse.getCodeFirstSupervisor())
                .replace("${nameSecondSupervisor}", tHeadSubjectAndContentSendMailResponse.getNameSecondSupervisor())
                .replace("${codeSecondSupervisor}", tHeadSubjectAndContentSendMailResponse.getCodeSecondSupervisor())
                .replace("${pathExamPaper}", tHeadSubjectAndContentSendMailResponse.getPathExamPaper());

        String footer = MailConstant.FOOTER;

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = null;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.toString());
            ClassPathResource resource = new ClassPathResource(MailConstant.LOGO_PATH);
            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(toEmail);
            mimeMessageHelper.setText(header + body + footer, true);
            mimeMessageHelper.setSubject(MailConstant.SUBJECT);
            mimeMessageHelper.addInline("logoImage", resource);
            mailSender.send(mimeMessage);

            PermissionDetail permissionDetail = new PermissionDetail();
            permissionDetail.setType("user");
            permissionDetail.setRole("writer");
            permissionDetail.setEmailAddress(toEmail);
            googleDriveManagerService.createPermissionForEmail(tHeadSubjectAndContentSendMailResponse.getPathExamPaper(), permissionDetail);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendEmailWhenHeadDepartmentCreateExamShift(
            HDSendMailWhenCreateExamShiftResponse hdSendMailWhenCreateExamShiftResponse, String password) {
        String[] toEmails = {
                hdSendMailWhenCreateExamShiftResponse.getAccountFptHeadSubject(),
                hdSendMailWhenCreateExamShiftResponse.getAccountFptFirstSupervisor(),
                hdSendMailWhenCreateExamShiftResponse.getAccountFptSecondSupervisor()
        };

        String header = MailConstant.HEADER
                .replace("${title}", "Thông báo ca thi đã được tạo");

        String body = MailConstant.BODY_CREATE_EXAM_SHIFT
                .replace("${examShiftCode}", hdSendMailWhenCreateExamShiftResponse.getExamShiftCode())
                .replace("${examDate}", DateTimeUtil.parseLongToString(hdSendMailWhenCreateExamShiftResponse.getExamDate()))
                .replace("${room}", hdSendMailWhenCreateExamShiftResponse.getRoom())
                .replace("${shift}", hdSendMailWhenCreateExamShiftResponse.getShift())
                .replace("${classSubjectCode}", hdSendMailWhenCreateExamShiftResponse.getClassSubjectCode())
                .replace("${subjectName}", hdSendMailWhenCreateExamShiftResponse.getSubjectName())
                .replace("${nameFirstSupervisor}", hdSendMailWhenCreateExamShiftResponse.getNameFirstSupervisor())
                .replace("${codeFirstSupervisor}", hdSendMailWhenCreateExamShiftResponse.getCodeFirstSupervisor())
                .replace("${nameSecondSupervisor}", hdSendMailWhenCreateExamShiftResponse.getNameSecondSupervisor())
                .replace("${codeSecondSupervisor}", hdSendMailWhenCreateExamShiftResponse.getCodeSecondSupervisor())
                .replace("${password}", password);

        String footer = MailConstant.FOOTER;

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = null;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.toString());
            ClassPathResource resource = new ClassPathResource(MailConstant.LOGO_PATH);
            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(toEmails);
            mimeMessageHelper.setText(header + body + footer, true);
            mimeMessageHelper.setSubject(MailConstant.SUBJECT);
            mimeMessageHelper.addInline("logoImage", resource);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendEmailWhenHeadSubjectCreateExamShift(HSSendMailToHeadDepartmentWhenCreateExamShiftResponse hsSendMailWhenCreateExamShiftResponse, String password) {

    }

}
