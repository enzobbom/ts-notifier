package com.javanauta.ts.notifier.infrastructure.email.sender;

import com.javanauta.ts.notifier.domain.exception.SendEmailException;
import com.javanauta.ts.notifier.domain.ports.email.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender javaMailSender;

    public void sendEmail(String sender, String senderName, String recipientEmail, String subject, String htmlEmailContent) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            mimeMessageHelper.setFrom(new InternetAddress(sender, senderName));
            mimeMessageHelper.setTo(InternetAddress.parse(recipientEmail));
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(htmlEmailContent, true);

            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new SendEmailException("Sending e-mail failed", e);
        }
    }
}
