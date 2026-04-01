package com.javanauta.ts.notifier.service.model;

import com.javanauta.ts.notifier.service.model.enums.NotificationStatus;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record Notification(
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
