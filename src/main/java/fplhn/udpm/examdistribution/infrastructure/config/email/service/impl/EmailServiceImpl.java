package fplhn.udpm.examdistribution.infrastructure.config.email.service.impl;

import fplhn.udpm.examdistribution.core.headsubject.examapproval.model.request.EASenEmailRejectExamPaper;
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

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = null;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.toString());
            ClassPathResource resource = new ClassPathResource(MailConstant.LOGO_PATH);
            mimeMessageHelper.setFrom(tHeadSubjectAndContentSendMailResponse.getAccountFptHeadSubject());
            mimeMessageHelper.setTo(toEmail);
            mimeMessageHelper.setText(body, true);
            mimeMessageHelper.setSubject(MailConstant.SUBJECT);
            mimeMessageHelper.addInline("logoImage", resource);
            mailSender.send(mimeMessage);

            PermissionDetail permissionDetail = new PermissionDetail();
            permissionDetail.setType("user");
            permissionDetail.setRole("reader");
            permissionDetail.setEmailAddress(toEmail);
            googleDriveManagerService.createPermissionForEmail(tHeadSubjectAndContentSendMailResponse.getPathExamPaper(), permissionDetail);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendEmailWhenRejectExamPaper(EASenEmailRejectExamPaper request) {
        String toEmail = request.getSendToEmailAddress();

        String header = MailConstant.HEADER
                .replace("${title}", "Thông báo đề thi bị từ chối.");

        String body = MailConstant.BODY_REJECT_EXAM_PAPER
                .replace("${examPaperCode}", request.getExamPaperCode())
                .replace("${timeSend}", DateTimeUtil.parseLongToString(request.getTimeReject().getTime()))
                .replace("${subjectName}", request.getSubjectName())
                .replace("${majorName}", request.getMajorName())
                .replace("${departmentName}", request.getDepartmentName());

        String footer = MailConstant.FOOTER;

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = null;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.toString());
            ClassPathResource resource = new ClassPathResource(MailConstant.LOGO_PATH);
            mimeMessageHelper.setFrom("lucvanluat@gmail.com");
            mimeMessageHelper.setTo(toEmail);
//            mimeMessageHelper.setBcc(request.getListEmailBcc());
            mimeMessageHelper.setText(header + body + footer, true);
            mimeMessageHelper.setSubject(MailConstant.SUBJECT);
            mimeMessageHelper.addInline("logoImage", resource);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


}
