package com.javanauta.ts.notifier.business.dto;

import com.javanauta.ts.notifier.business.enums.NotificationStatusEnum;
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
    private Instant dueDateTime;
    private String userEmail;
    private Instant modificationDateTime;
    private NotificationStatusEnum notificationStatusEnum;
}
