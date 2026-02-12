package com.javanauta.ts.notifier.infrastructure.notification;

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
public class JavaMailSenderNotifier {

    private final JavaMailSender javaMailSender;

    public void sendEmail(String sender, String senderName, String recipientEmail, String subject, String htmlEmailContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        mimeMessageHelper.setFrom(new InternetAddress(sender, senderName));
        mimeMessageHelper.setTo(InternetAddress.parse(recipientEmail));
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(htmlEmailContent, true);

        javaMailSender.send(message);
    }
}
