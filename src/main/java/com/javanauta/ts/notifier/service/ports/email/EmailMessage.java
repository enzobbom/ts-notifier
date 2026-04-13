package com.javanauta.ts.notifier.service.ports.email;

import lombok.Builder;

@Builder
public record EmailMessage(
        String sender,
        String senderName,
        String recipient,
        String subject,
        String body
) {}

