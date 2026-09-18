package com.javanauta.ts.notifier.rabbitmq;

import com.javanauta.ts.events.notification.NotificationRequestEvent;
import com.javanauta.ts.events.notification.messaging.Exchanges;
import com.javanauta.ts.events.notification.messaging.Queues;
import com.javanauta.ts.events.notification.messaging.RoutingKeys;
import com.javanauta.ts.notifier.adapters.in.messaging.NotificationFailedRecoverer;
import com.javanauta.ts.notifier.adapters.in.messaging.RabbitNotificationRequestListener;
import com.javanauta.ts.notifier.adapters.in.messaging.config.RabbitInConfig;
import com.javanauta.ts.notifier.adapters.in.messaging.mapper.NotificationEventMapperImpl;
import com.javanauta.ts.notifier.adapters.in.messaging.validation.NotificationEventValidator;
import com.javanauta.ts.notifier.adapters.shared.config.RabbitCommonConfig;
import com.javanauta.ts.notifier.application.usecase.NotificationFailureService;
import com.javanauta.ts.notifier.application.usecase.SendNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.rabbitmq.RabbitMQContainer;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest(
        classes = {
                RabbitNotificationRequestListener.class,
                RabbitCommonConfig.class,
                RabbitInConfig.class,
                NotificationEventValidator.class,
                NotificationEventMapperImpl.class,
                RabbitNotificationRequestListenerIT.RabbitTestTopology.class
        }
)
@EnableAutoConfiguration
@EnableRabbit
@Testcontainers
class RabbitNotificationRequestListenerIT {
    @Container
    @ServiceConnection
    static final org.testcontainers.rabbitmq.RabbitMQContainer RABBITMQ =
            new RabbitMQContainer("rabbitmq:4-management");

    @MockitoBean
    private SendNotificationService notificationService;

    @MockitoBean
    private NotificationFailedRecoverer notificationFailedRecoverer;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void handleNotificationRequested_shouldMapAndDelegateValidEvent() {
        NotificationRequestEvent event =
                NotificationRequestEvent.create(
                        "task-123",
                        "Buy groceries",
                        "Milk, bread and eggs",
                        Instant.parse("2026-09-18T15:30:00Z"),
                        "recipient@example.com",
                        "Europe/Dublin"
                );

        rabbitTemplate.convertAndSend(
                Exchanges.NOTIFICATION,
                RoutingKeys.NOTIFICATION_REQUEST,
                event
        );

        // Wait until the listener has consumed the message.
        await().untilAsserted(() ->
                verify(notificationService).sendNotification(
                        argThat(result ->
                                result.id().equals(event.taskId())
                                        && result.title().equals(event.taskName())
                                        && result.description().equals(event.taskDescription())
                                        && result.scheduledDateTime().equals(event.taskScheduledDateTime())
                                        && result.recipient().equals(event.taskRecipient())
                                        && result.timeZoneId().equals(ZoneId.of(event.taskZoneId()))
                        )
                )
        );
    }

    @Test
    void handleNotificationRequested_shouldNotDelegateWhenEventIsInvalid() {
        NotificationRequestEvent event =
                new NotificationRequestEvent(
                        UUID.randomUUID(),
                        Instant.now(),
                        "",
                        "",
                        "Description",
                        Instant.now(),
                        "not-an-email",
                        "Europe/Dublin"
                );

        rabbitTemplate.convertAndSend(
                Exchanges.NOTIFICATION,
                RoutingKeys.NOTIFICATION_REQUEST,
                event
        );

        // Wait until the listener has consumed the message.
        await().untilAsserted(() ->
                verifyNoInteractions(notificationService)
        );
    }

    @TestConfiguration
    static class RabbitTestTopology {

        @Bean
        TopicExchange notificationExchange() {
            return new TopicExchange(Exchanges.NOTIFICATION);
        }

        @Bean
        Queue notificationRequestQueue() {
            return new Queue(Queues.NOTIFICATION_REQUEST);
        }

        @Bean
        Binding notificationRequestBinding(
                Queue notificationRequestQueue,
                TopicExchange notificationExchange) {

            return BindingBuilder
                    .bind(notificationRequestQueue)
                    .to(notificationExchange)
                    .with(RoutingKeys.NOTIFICATION_REQUEST);
        }
    }
}
