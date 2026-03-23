package com.javanauta.ts.notifier.presentation.dto;

import com.javanauta.ts.notifier.domain.model.enums.NotificationStatus;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDTO {

    private String id;
    private String name;
    private String description;
    private Instant creationDateTime;
    private Instant scheduledDateTime;
    private String userEmail;
    private Instant modificationDateTime;
    private NotificationStatus notificationStatus;
    private String timeZoneId;
}
