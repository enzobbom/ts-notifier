package com.javanauta.ts.notifier.infrastructure.email.sender;

import com.javanauta.ts.notifier.domain.ports.email.EmailSender;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoOpEmailSender implements EmailSender {
    public void sendEmail(String sender, String senderName, String recipientEmail, String subject, String htmlEmailContent) {
        log.debug("Skipped sending notification e-mail");
    }
}
