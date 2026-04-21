package com.javanauta.ts.notifier.application.usecase;

import com.javanauta.ts.notifier.application.command.NotifyTaskCommand;
import com.javanauta.ts.notifier.application.port.email.EmailComposer;
import com.javanauta.ts.notifier.application.port.email.EmailMessage;
import com.javanauta.ts.notifier.application.port.email.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendNotificationService {
    private final EmailComposer emailComposer;
    private final EmailSender emailSender;

    public void sendNotification(NotifyTaskCommand notifyTaskCommand) {
        if (!notifyTaskCommand.canBeNotified()) {return;}
        sendEmailNotification(notifyTaskCommand);
    }

    private void sendEmailNotification(NotifyTaskCommand notifyTaskCommand) {
        emailSender.send(emailComposer.compose(notifyTaskCommand));
        log.info("Task '{}' was successfully notified by email", notifyTaskCommand.id());
    }
}
