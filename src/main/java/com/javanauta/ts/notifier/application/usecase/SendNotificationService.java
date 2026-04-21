package com.javanauta.ts.notifier.application.usecase;

import com.javanauta.ts.notifier.application.command.NotifyTaskCommand;
import com.javanauta.ts.notifier.application.port.email.EmailComposer;
import com.javanauta.ts.notifier.application.port.email.EmailMessage;
import com.javanauta.ts.notifier.application.port.email.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendNotificationService {
    private final EmailComposer emailComposer;
    private final EmailSender emailSender;

    @Value("${sending.email.sender}")
    public String sendingEmail;

    @Value("${sending.email.senderName}")
    public String sendingName;

    private static final String EMAIL_SUBJECT = "Task NotifyTaskCommand";

    public void notify(NotifyTaskCommand notifyTaskCommand) {
        if (!notifyTaskCommand.canBeNotified()) {
            return;
        }

        notifyByEmail(notifyTaskCommand);
    }

    private void notifyByEmail(NotifyTaskCommand notifyTaskCommand) {
        String emailBody = emailComposer.compose(notifyTaskCommand);

        EmailMessage emailMessage = EmailMessage.builder()
                .sender(sendingEmail)
                .senderName(sendingName)
                .recipient(notifyTaskCommand.recipient())
                .subject(EMAIL_SUBJECT)
                .body(emailBody)
                .build();

        emailSender.send(emailMessage);

        log.info("Task '{}' was successfully notified by email", notifyTaskCommand.id());
    }
}
