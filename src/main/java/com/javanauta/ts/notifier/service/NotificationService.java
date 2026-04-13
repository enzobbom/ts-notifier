package com.javanauta.ts.notifier.service;

import com.javanauta.ts.notifier.service.mapper.NotificationMapper;
import com.javanauta.ts.notifier.service.ports.email.EmailMessage;
import com.javanauta.ts.notifier.service.model.Notification;
import com.javanauta.ts.notifier.service.ports.email.EmailComposer;
import com.javanauta.ts.notifier.service.ports.email.EmailSender;
import com.javanauta.ts.notifier.presentation.dto.NotifyTaskRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationMapper notificationMapper;
    private final EmailComposer emailComposer;
    private final EmailSender emailSender;

    @Value("${sending.email.sender}")
    public String sendingEmail;

    @Value("${sending.email.senderName}")
    public String sendingName;

    private static final String EMAIL_SUBJECT = "Task Notification";

    public void notify(NotifyTaskRequestDTO notifyTaskRequestDTO) {
        Notification notification = notificationMapper.toDomain(notifyTaskRequestDTO);

        if (!notification.canBeNotified()) {
            return;
        }

        notifyByEmail(notification);
    }

    private void notifyByEmail(Notification notification) {
        String emailBody = emailComposer.compose(notification);

        EmailMessage emailMessage = EmailMessage.builder()
                .sender(sendingEmail)
                .senderName(sendingName)
                .recipient(notification.recipient())
                .subject(EMAIL_SUBJECT)
                .body(emailBody)
                .build();

        emailSender.send(emailMessage);

        log.info("Task '{}' was successfully notified by email", notification.id());
    }
}
