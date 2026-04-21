package com.javanauta.ts.notifier.infrastructure.email.composer;

import com.javanauta.ts.notifier.application.command.NotifyTaskCommand;
import com.javanauta.ts.notifier.application.port.email.EmailComposer;
import com.javanauta.ts.notifier.application.port.email.EmailMessage;
import com.javanauta.ts.notifier.infrastructure.config.email.EmailProperties;
import lombok.AllArgsConstructor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@AllArgsConstructor
public class ThymeleafEmailComposer implements EmailComposer {
    private final TemplateEngine templateEngine;
    private final EmailProperties emailProperties;

    private static final String EMAIL_SUBJECT = "Task Notification";

    public EmailMessage compose(NotifyTaskCommand notifyTaskCommand) {
        Context context = new Context();
        context.setVariable("name", notifyTaskCommand.title());
        context.setVariable("scheduledDateTime", notifyTaskCommand.getFormattedScheduledTime());
        context.setVariable("description", notifyTaskCommand.description());

        return EmailMessage.builder()
                .sender(emailProperties.getSender())
                .senderName(emailProperties.getSenderName())
                .recipient(notifyTaskCommand.recipient())
                .subject(EMAIL_SUBJECT)
                .body(templateEngine.process("email/thymeleaf", context))
                .build();
    }
}
