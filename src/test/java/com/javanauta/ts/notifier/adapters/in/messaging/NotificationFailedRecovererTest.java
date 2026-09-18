package com.javanauta.ts.notifier.adapters.in.messaging;

import com.javanauta.ts.events.notification.NotificationRequestEvent;
import com.javanauta.ts.notifier.adapters.out.email.exception.EmailException;
import com.javanauta.ts.notifier.adapters.out.email.exception.enums.EmailExceptionCode;
import com.javanauta.ts.notifier.application.data.NotificationResultDetails;
import com.javanauta.ts.notifier.application.data.enums.NotificationResult;
import com.javanauta.ts.notifier.application.usecase.NotificationFailureService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationFailedRecovererTest {

    @Mock
    private MessageConverter messageConverter;

    @Mock
    private NotificationFailureService notificationFailureService;

    @Mock
    private ConstraintViolation<NotificationRequestEvent> constraintViolation;

    @Mock
    private Path propertyPath;

    private NotificationFailedRecoverer recoverer;

    @BeforeEach
    void setUp() {
        recoverer = new NotificationFailedRecoverer(
                messageConverter,
                notificationFailureService
        );
    }

    @Test
    void recover_shouldIgnoreMessageWhenFailureWasMessageConversionException() {
        Message message = new Message(
                "invalid payload".getBytes(StandardCharsets.UTF_8)
        );

        MessageConversionException exception =
                new MessageConversionException("Invalid message");

        recoverer.recover(message, exception);

        verifyNoInteractions(messageConverter, notificationFailureService);
    }

    @Test
    void recover_shouldIgnoreMessageWhenConversionFailsInsideRecoverer() {
        Message message = notificationRequestMessage();

        when(messageConverter.fromMessage(message))
                .thenThrow(new MessageConversionException("Could not convert message"));

        recoverer.recover(
                message,
                new RuntimeException("Listener failure")
        );

        verify(messageConverter).fromMessage(message);
        verifyNoInteractions(notificationFailureService);
    }

    @Test
    void recover_shouldIgnoreMessageWhenValidationFails() {
        NotificationRequestEvent event = notificationRequestEvent();
        Message message = notificationRequestMessage();

        when(messageConverter.fromMessage(message)).thenReturn(event);
        when(constraintViolation.getPropertyPath()).thenReturn(propertyPath);
        when(propertyPath.toString()).thenReturn("taskRecipient");
        when(constraintViolation.getMessage())
                .thenReturn("must be a valid email address");

        ConstraintViolationException exception =
                new ConstraintViolationException(Set.of(constraintViolation));

        recoverer.recover(message, exception);

        verify(messageConverter).fromMessage(message);
        verifyNoInteractions(notificationFailureService);
    }

    @Test
    void recover_shouldPublishTemporaryFailureWhenInfrastructureIsUnavailable() {
        NotificationRequestEvent event = notificationRequestEvent();
        Message message = notificationRequestMessage();

        when(messageConverter.fromMessage(message)).thenReturn(event);

        EmailException exception = new EmailException(
                EmailExceptionCode.INFRASTRUCTURE_UNAVAILABLE,
                "SMTP server unavailable",
                null
        );

        recoverer.recover(message, exception);

        verifyFailureResult(
                event,
                NotificationResult.TEMPORARY_FAILURE,
                "SMTP server unavailable"
        );
    }

    @Test
    void recover_shouldPublishPermanentFailureWhenEmailHasInternalError() {
        NotificationRequestEvent event = notificationRequestEvent();
        Message message = notificationRequestMessage();

        when(messageConverter.fromMessage(message)).thenReturn(event);

        EmailException exception = new EmailException(
                EmailExceptionCode.INTERNAL_ERROR,
                "Could not create e-mail",
                null
        );

        recoverer.recover(message, exception);

        verifyFailureResult(
                event,
                NotificationResult.PERMANENT_FAILURE,
                "Could not create e-mail"
        );
    }

    @Test
    void recover_shouldPublishPermanentFailureWhenUnexpectedExceptionOccurs() {
        NotificationRequestEvent event = notificationRequestEvent();
        Message message = notificationRequestMessage();

        when(messageConverter.fromMessage(message)).thenReturn(event);

        RuntimeException exception =
                new RuntimeException("Unexpected failure");

        recoverer.recover(message, exception);

        verifyFailureResult(
                event,
                NotificationResult.PERMANENT_FAILURE,
                "Notification could not be sent due to an internal error"
        );
    }

    private void verifyFailureResult(
            NotificationRequestEvent event,
            NotificationResult expectedResult,
            String expectedErrorMessage) {

        ArgumentCaptor<NotificationResultDetails> resultCaptor =
                ArgumentCaptor.forClass(NotificationResultDetails.class);

        verify(notificationFailureService)
                .handleNotificationFailure(resultCaptor.capture());

        NotificationResultDetails result = resultCaptor.getValue();

        assertThat(result.taskId()).isEqualTo(event.taskId());
        assertThat(result.notificationResult()).isEqualTo(expectedResult);
        assertThat(result.errorMessage()).isEqualTo(expectedErrorMessage);
    }

    private NotificationRequestEvent notificationRequestEvent() {
        return new NotificationRequestEvent(
                UUID.randomUUID(),
                Instant.parse("2026-09-18T10:00:00Z"),
                "task-123",
                "Test task",
                "Test description",
                Instant.parse("2026-09-18T12:00:00Z"),
                "recipient@example.com",
                "Europe/Dublin"
        );
    }

    private Message notificationRequestMessage() {
        return new Message(
                "notification request".getBytes(StandardCharsets.UTF_8)
        );
    }
}