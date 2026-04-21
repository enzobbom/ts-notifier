package com.javanauta.ts.notifier.presentation.dto;

import com.javanauta.ts.notifier.application.command.enums.NotificationStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Builder
@Jacksonized // Ensures Jackson uses Builder in Record class
public record NotifyTaskRequestDTO(
        @NotBlank String id,
        @NotBlank String name,
        String description,
        @NotNull Instant creationDateTime,
        @NotNull Instant scheduledDateTime,
        @NotBlank @Email String userEmail,
        @NotNull NotificationStatus notificationStatus,
        @NotBlank String timeZoneId
) {}
