package com.javanauta.ts.notifier.application.service;

import com.javanauta.ts.notifier.domain.model.enums.NotificationStatus;
import com.javanauta.ts.notifier.domain.ports.email.EmailComposer;
import com.javanauta.ts.notifier.domain.ports.email.EmailSender;
import com.javanauta.ts.notifier.presentation.dto.TaskDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final EmailComposer emailComposer;
    private final EmailSender emailSender;

    @Value("${sending.email.sender}")
    public String sender;

    @Value("${sending.email.senderName}")
    public String senderName;

    public void sendEmail(TaskDTO taskDTO) {
        if (taskDTO.getNotificationStatus() != NotificationStatus.PENDING) {
            return;
        }

        String taskName = taskDTO.getName();
        ZoneId zoneId = ZoneId.of(taskDTO.getTimeZoneId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").withZone(zoneId);

        String emailContent = emailComposer.composeEmail(
                taskName,
                formatter.format(taskDTO.getScheduledDateTime()),
                taskDTO.getDescription());

        emailSender.sendEmail(
                sender,
                senderName,
                taskDTO.getUserEmail(),
                "Task notification",
                emailContent
        );

        log.info("Task '{}' was successfully notified by email", taskName);
        log.info("Task '{}' was successfully notified by email", taskName);
    }
}
