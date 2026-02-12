package com.javanauta.ts.notifier.business;

import com.javanauta.ts.notifier.business.dto.TaskDTO;
import com.javanauta.ts.notifier.business.enums.NotificationStatusEnum;
import com.javanauta.ts.notifier.infrastructure.exception.EmailException;
import com.javanauta.ts.notifier.infrastructure.notification.JavaMailSenderNotifier;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Slf4j
public class SmtpEmailService implements EmailService {

    private final JavaMailSenderNotifier javaMailSenderNotifier;
    private final TemplateEngine templateEngine;

    @Value("${sending.email.sender}")
    public String sender;

    @Value("${sending.email.senderName}")
    public String senderName;

    public void sendEmail(TaskDTO taskDTO) {
        if (taskDTO.getNotificationStatusEnum() != NotificationStatusEnum.PENDING) {
            return;
        }

        String taskName = taskDTO.getName();
        ZoneId zoneId = ZoneId.of(taskDTO.getTimeZoneId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(zoneId);

        Context context = new Context();
        context.setVariable("name", taskName);
        context.setVariable("scheduledDateTime", formatter.format(taskDTO.getScheduledDateTime()));
        context.setVariable("description", taskDTO.getDescription());

        String emailContent = templateEngine.process("notification", context);

        try {
            javaMailSenderNotifier.sendEmail(
                    sender,
                    senderName,
                    taskDTO.getUserEmail(),
                    "Task notification",
                    emailContent
            );

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException(String.format("Failed to notify Task '%s' by email", taskName), e);
        }

        log.info("Task '{}' was successfully notified by email", taskName);
    }
}
