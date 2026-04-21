package com.javanauta.ts.notifier.application.command;

import com.javanauta.ts.notifier.application.command.enums.NotificationStatus;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record NotifyTaskCommand(
        String id,
        String title,
        String description,
        Instant creationDateTime,
        Instant scheduledDateTime,
        String recipient,
        NotificationStatus status,
        ZoneId timeZoneId
) {
    private static final String DATETIME_FORMAT = "dd-MM-yyyy HH:mm:ss";

    public boolean canBeNotified() {
        return NotificationStatus.PENDING.equals(status);
    }

    public String getFormattedScheduledTime() {
        return DateTimeFormatter.ofPattern(DATETIME_FORMAT)
                .withZone(timeZoneId)
                .format(scheduledDateTime);
    }
}
