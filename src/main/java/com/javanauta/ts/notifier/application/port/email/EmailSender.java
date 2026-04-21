package com.javanauta.ts.notifier.application.port.email;

public interface EmailSender {
    public void send(EmailMessage message);
}
