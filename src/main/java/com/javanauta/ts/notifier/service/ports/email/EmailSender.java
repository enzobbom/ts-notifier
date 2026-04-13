package com.javanauta.ts.notifier.service.ports.email;

public interface EmailSender {
    public void send(EmailMessage message);
}
