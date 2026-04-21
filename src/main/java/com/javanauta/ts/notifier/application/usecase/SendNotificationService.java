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

    public void notify(NotifyTaskCommand notifyTaskCommand) {
        if (!notifyTaskCommand.canBeNotified()) {return;}
        notifyByEmail(notifyTaskCommand);
    }

    private void notifyByEmail(NotifyTaskCommand notifyTaskCommand) {
        EmailMessage message = emailComposer.compose(notifyTaskCommand);
        emailSender.send(message);

        log.info("Task '{}' was successfully notified by email", notifyTaskCommand.id());
    }
}
