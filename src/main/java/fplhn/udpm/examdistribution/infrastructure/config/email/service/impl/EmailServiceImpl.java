package fplhn.udpm.examdistribution.infrastructure.config.email.service.impl;

import fplhn.udpm.examdistribution.infrastructure.config.email.service.EmailService;
import fplhn.udpm.examdistribution.infrastructure.constant.MailConstant;
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

    @Override
    public void sendEmailPublicMockExamPaper(String[] listEmailBcc) {

        String toEmail = "nguyenvimanhnqt@gmail.com";

        String body = MailConstant.BODY;

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = null;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.toString());
            ClassPathResource resource = new ClassPathResource(MailConstant.LOGO_PATH);
            mimeMessageHelper.setFrom("nguyenvimanhnqt@gmail.com");
            mimeMessageHelper.setTo(toEmail);
            mimeMessageHelper.setBcc(listEmailBcc);
            mimeMessageHelper.setText(body, true);
            mimeMessageHelper.setSubject(MailConstant.SUBJECT);
            mimeMessageHelper.addInline("logoImage", resource);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
