package com.javanauta.ts.notifier.domain.ports.email;

public interface EmailSender {
    public void sendEmail(String sender, String senderName, String recipientEmail, String subject, String content);
}
