package com.javanauta.ts.notifier.infrastructure.email.sender;

import com.javanauta.ts.notifier.application.port.email.EmailMessage;
import com.javanauta.ts.notifier.application.port.email.EmailSender;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoOpEmailSender implements EmailSender {
    public void send(EmailMessage message) {
        log.debug("Skipped sending notification e-mail");
    }
}
