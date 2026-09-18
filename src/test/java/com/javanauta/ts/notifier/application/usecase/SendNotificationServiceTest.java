package com.javanauta.ts.notifier.application.usecase;

import com.javanauta.ts.notifier.adapters.out.email.data.EmailMessage;
import com.javanauta.ts.notifier.application.command.NotifyTaskCommand;
import com.javanauta.ts.notifier.application.data.NotificationResultDetails;
import com.javanauta.ts.notifier.application.data.enums.NotificationResult;
import com.javanauta.ts.notifier.ports.out.email.EmailComposer;
import com.javanauta.ts.notifier.ports.out.email.EmailSender;
import com.javanauta.ts.notifier.ports.out.messaging.NotificationCompletedPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendNotificationServiceTest {
    @Mock
    private EmailComposer emailComposer;

    @Mock
    private EmailSender emailSender;

    @Mock
    private NotificationCompletedPublisher notificationCompletedPublisher;

    @InjectMocks
    private SendNotificationService notificationService;

    @Test
    void sendNotification_shouldComposeAndSendEmailAndPublishSuccess() {
        NotifyTaskCommand command = new NotifyTaskCommand(
                "task-123",
                "Test task",
                "Test description",
                Instant.parse("2026-09-18T10:00:00Z"),
                "recipient@example.com",
                ZoneId.of("Europe/Dublin")
        );

        EmailMessage emailMessage = EmailMessage.builder()
                .sender("sender@example.com")
                .senderName("Task Scheduler")
                .recipient(command.recipient())
                .subject("Task Notification")
                .body("email body")
                .build();

        when(emailComposer.compose(command)).thenReturn(emailMessage);

        notificationService.sendNotification(command);

        verify(emailComposer).compose(command);
        verify(emailSender).send(emailMessage);

        ArgumentCaptor<NotificationResultDetails> resultCaptor =
                ArgumentCaptor.forClass(NotificationResultDetails.class);

        verify(notificationCompletedPublisher)
                .publishNotificationCompleted(resultCaptor.capture());

        NotificationResultDetails result = resultCaptor.getValue();

        assertThat(result.taskId()).isEqualTo(command.id());
        assertThat(result.notificationResult()).isEqualTo(NotificationResult.SUCCESS);
        assertThat(result.errorMessage()).isNull();
    }

    @Test
    void sendNotification_shouldNotPublishCompletionWhenEmailSendingFails() {
        NotifyTaskCommand command = new NotifyTaskCommand(
                "task-123",
                "Test task",
                "Test description",
                Instant.parse("2026-09-18T10:00:00Z"),
                "recipient@example.com",
                ZoneId.of("Europe/Dublin")
        );

        EmailMessage emailMessage = EmailMessage.builder()
                .sender("sender@example.com")
                .senderName("Task Scheduler")
                .recipient(command.recipient())
                .subject("Task Notification")
                .body("email body")
                .build();

        RuntimeException exception = new RuntimeException("Email sending failed");

        when(emailComposer.compose(command)).thenReturn(emailMessage);
        doThrow(exception).when(emailSender).send(emailMessage);

        assertThatThrownBy(() -> notificationService.sendNotification(command))
                .isSameAs(exception);

        verify(emailComposer).compose(command);
        verify(emailSender).send(emailMessage);
        verifyNoInteractions(notificationCompletedPublisher);
    }
}