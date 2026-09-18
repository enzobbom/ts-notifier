package com.javanauta.ts.notifier.email.composer;

import com.javanauta.ts.notifier.adapters.out.email.composer.ThymeleafEmailComposer;
import com.javanauta.ts.notifier.adapters.out.email.config.EmailConfig;
import com.javanauta.ts.notifier.adapters.out.email.config.EmailProperties;
import com.javanauta.ts.notifier.adapters.out.email.data.EmailMessage;
import com.javanauta.ts.notifier.application.command.NotifyTaskCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {
                EmailConfig.class,
        }
)
@EnableAutoConfiguration
class ThymeleafEmailComposerIT {
    @Autowired
    private ThymeleafEmailComposer emailComposer;

    @Autowired
    private EmailProperties emailProperties;

    @Test
    void compose_shouldCreateEmailMessageWithExpectedContent() {
        NotifyTaskCommand command = new NotifyTaskCommand(
                "task-123",
                "Buy groceries",
                "Milk, bread and eggs",
                Instant.parse("2026-09-18T15:30:00Z"),
                "recipient@example.com",
                ZoneId.of("Europe/Dublin")
        );

        EmailMessage result = emailComposer.compose(command);

        assertThat(result.sender()).isEqualTo(emailProperties.getSender());
        assertThat(result.senderName()).isEqualTo(emailProperties.getSenderName());
        assertThat(result.recipient()).isEqualTo(command.recipient());
        assertThat(result.subject()).isEqualTo("Task Notification");

        assertThat(result.body())
                .contains("Task Notification")
                .contains("Buy groceries")
                .contains("18-09-2026 16:30:00")
                .contains("Milk, bread and eggs");
    }
}
