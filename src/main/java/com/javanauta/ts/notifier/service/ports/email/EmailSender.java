package com.javanauta.ts.notifier.service.ports.email;

import com.javanauta.ts.notifier.service.model.EmailMessage;

public interface EmailSender {
    public void send(EmailMessage message);
}
