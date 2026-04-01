package com.javanauta.ts.notifier.infrastructure.email.sender;

import com.javanauta.ts.notifier.service.exception.EmailException;
import com.javanauta.ts.notifier.service.model.EmailMessage;
import com.javanauta.ts.notifier.service.ports.email.EmailSender;
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

    public void send(EmailMessage messageToSend) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            mimeMessageHelper.setFrom(new InternetAddress(messageToSend.sender(), messageToSend.senderName()));
            mimeMessageHelper.setTo(InternetAddress.parse(messageToSend.recipient()));
            mimeMessageHelper.setSubject(messageToSend.subject());
            mimeMessageHelper.setText(messageToSend.body(), true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("Sending e-mail failed", e);
        }
    }
}
