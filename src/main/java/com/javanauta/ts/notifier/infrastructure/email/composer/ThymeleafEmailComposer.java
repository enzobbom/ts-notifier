package com.javanauta.ts.notifier.infrastructure.email.composer;

import com.javanauta.ts.notifier.application.command.NotifyTaskCommand;
import com.javanauta.ts.notifier.application.port.email.EmailComposer;
import lombok.AllArgsConstructor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@AllArgsConstructor
public class ThymeleafEmailComposer implements EmailComposer {
    private final TemplateEngine templateEngine;

    public String compose(NotifyTaskCommand notifyTaskCommand) {
        Context context = new Context();
        context.setVariable("name", notifyTaskCommand.title());
        context.setVariable("scheduledDateTime", notifyTaskCommand.getFormattedScheduledTime());
        context.setVariable("description", notifyTaskCommand.description());

        return templateEngine.process("email/thymeleaf", context);
    }
}
