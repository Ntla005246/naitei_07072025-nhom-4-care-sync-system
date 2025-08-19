package com.example.backend.service;

import com.example.backend.constant.ApiConstants;
import com.example.backend.constant.MessageConstants;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendVerificationEmail(String recipientEmail, String userName, String token) {
        try {
            String host = "http://localhost:8080";
            String verificationUrl = host + ApiConstants.AUTH_ENDPOINT + "/verify-email?token="
                    + token;
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("verificationUrl", verificationUrl);

            String htmlContent = templateEngine.process("email-verification.html", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(recipientEmail);
            helper.setSubject(MessageConstants.EMAIL_SUBJECT_EMAIL_VERIFICATION);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(MessageConstants.EMAIL_SENDING_FAILED, e);
        }
    }
}
