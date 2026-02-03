package com.javanauta.ts.notifier.business;

import com.javanauta.ts.notifier.business.dto.TaskDTO;
import com.javanauta.ts.notifier.business.enums.NotificationStatusEnum;
import com.javanauta.ts.notifier.infrastructure.exception.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${sending.email.sender}")
    public String sender;

    @Value("${sending.email.senderName}")
    public String senderName;

    public void sendEmail(TaskDTO taskDTO) {
        if (taskDTO.getNotificationStatusEnum() != NotificationStatusEnum.PENDING) {return;}

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            mimeMessageHelper.setFrom(new InternetAddress(sender, senderName));
            mimeMessageHelper.setTo(InternetAddress.parse(taskDTO.getUserEmail()));
            mimeMessageHelper.setSubject("Task notification");

            ZoneId zoneId = ZoneId.of(taskDTO.getTimeZoneId());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(zoneId);

            Context context = new Context();
            context.setVariable("name", taskDTO.getName());
            context.setVariable("scheduledDateTime", formatter.format(taskDTO.getScheduledDateTime()));
            context.setVariable("description", taskDTO.getDescription());
            String template = templateEngine.process("notification", context);
            mimeMessageHelper.setText(template, true);

            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException(String.format("Error sending email: '%s'", e.getCause()));
        }
    }
}
