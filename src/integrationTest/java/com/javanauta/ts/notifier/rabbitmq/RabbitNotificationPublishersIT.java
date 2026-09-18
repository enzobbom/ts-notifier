package com.javanauta.ts.notifier.rabbitmq;

import com.javanauta.ts.events.notification.NotificationCompletedEvent;
import com.javanauta.ts.events.notification.NotificationFailedEvent;
import com.javanauta.ts.events.notification.enums.NotificationFailureType;
import com.javanauta.ts.events.notification.messaging.Exchanges;
import com.javanauta.ts.events.notification.messaging.Queues;
import com.javanauta.ts.events.notification.messaging.RoutingKeys;
import com.javanauta.ts.notifier.adapters.out.messaging.RabbitNotificationCompletedPublisher;
import com.javanauta.ts.notifier.adapters.out.messaging.RabbitNotificationFailedPublisher;
import com.javanauta.ts.notifier.adapters.shared.config.RabbitCommonConfig;
import com.javanauta.ts.notifier.application.data.NotificationResultDetails;
import com.javanauta.ts.notifier.application.data.enums.NotificationResult;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.rabbitmq.RabbitMQContainer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {
                RabbitCommonConfig.class,
                RabbitNotificationCompletedPublisher.class,
                RabbitNotificationFailedPublisher.class,
                RabbitNotificationPublishersIT.RabbitTestTopology.class
        }
)
@EnableAutoConfiguration
@Testcontainers
class RabbitNotificationPublishersIT {
    @Container
    @ServiceConnection
    static final org.testcontainers.rabbitmq.RabbitMQContainer RABBITMQ =
            new RabbitMQContainer("rabbitmq:4-management");

    @Autowired
    private RabbitNotificationCompletedPublisher completedPublisher;

    @Autowired
    private RabbitNotificationFailedPublisher failedPublisher;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void publishNotificationCompleted_shouldPublishExpectedEvent() {
        String taskId = "task-123";

        completedPublisher.publishNotificationCompleted(
                new NotificationResultDetails(
                        taskId,
                        NotificationResult.SUCCESS,
                        null
                )
        );

        NotificationCompletedEvent event =
                (NotificationCompletedEvent) rabbitTemplate.receiveAndConvert(
                        Queues.NOTIFICATION_COMPLETED,
                        5_000
                );

        assertThat(event).isNotNull();
        assertThat(event.taskId()).isEqualTo(taskId);
        assertThat(event.eventId()).isNotNull();
        assertThat(event.occurredAt()).isNotNull();
    }

    @Test
    void publishNotificationFailed_shouldPublishPermanentFailureEvent() {
        String taskId = "task-123";
        String errorMessage = "Could not create e-mail";

        failedPublisher.publishNotificationFailed(
                new NotificationResultDetails(
                        taskId,
                        NotificationResult.PERMANENT_FAILURE,
                        errorMessage
                )
        );

        NotificationFailedEvent event =
                (NotificationFailedEvent) rabbitTemplate.receiveAndConvert(
                        Queues.NOTIFICATION_FAILED,
                        5_000
                );

        assertThat(event).isNotNull();
        assertThat(event.taskId()).isEqualTo(taskId);
        assertThat(event.failureType()).isEqualTo(NotificationFailureType.PERMANENT);
        assertThat(event.error()).isEqualTo(errorMessage);
        assertThat(event.eventId()).isNotNull();
        assertThat(event.occurredAt()).isNotNull();
    }

    @Test
    void publishNotificationFailed_shouldPublishTemporaryFailureEvent() {
        String taskId = "task-123";
        String errorMessage = "SMTP server unavailable";

        failedPublisher.publishNotificationFailed(
                new NotificationResultDetails(
                        taskId,
                        NotificationResult.TEMPORARY_FAILURE,
                        errorMessage
                )
        );

        NotificationFailedEvent event =
                (NotificationFailedEvent) rabbitTemplate.receiveAndConvert(
                        Queues.NOTIFICATION_FAILED,
                        5_000
                );

        assertThat(event).isNotNull();
        assertThat(event.taskId()).isEqualTo(taskId);
        assertThat(event.failureType()).isEqualTo(NotificationFailureType.TEMPORARY);
        assertThat(event.error()).isEqualTo(errorMessage);
        assertThat(event.eventId()).isNotNull();
        assertThat(event.occurredAt()).isNotNull();
    }

    @TestConfiguration
    static class RabbitTestTopology {

        @Bean
        TopicExchange notificationExchange() {
            return new TopicExchange(Exchanges.NOTIFICATION);
        }

        @Bean
        Queue notificationCompletedQueue() {
            return new Queue(Queues.NOTIFICATION_COMPLETED);
        }

        @Bean
        Binding notificationCompletedBinding(
                Queue notificationCompletedQueue,
                TopicExchange notificationExchange) {

            return BindingBuilder
                    .bind(notificationCompletedQueue)
                    .to(notificationExchange)
                    .with(RoutingKeys.NOTIFICATION_COMPLETED);
        }

        @Bean
        Queue notificationFailedQueue() {
            return new Queue(Queues.NOTIFICATION_FAILED);
        }

        @Bean
        Binding notificationFailedBinding(
                Queue notificationFailedQueue,
                TopicExchange notificationExchange) {

            return BindingBuilder
                    .bind(notificationFailedQueue)
                    .to(notificationExchange)
                    .with(RoutingKeys.NOTIFICATION_FAILED);
        }
    }
}
