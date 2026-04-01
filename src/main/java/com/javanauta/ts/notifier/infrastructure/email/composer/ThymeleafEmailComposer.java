package com.javanauta.ts.notifier.infrastructure.email.composer;

import com.javanauta.ts.notifier.service.model.Notification;
import com.javanauta.ts.notifier.service.ports.email.EmailComposer;
import lombok.AllArgsConstructor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@AllArgsConstructor
public class ThymeleafEmailComposer implements EmailComposer {
    private final TemplateEngine templateEngine;

    public String compose(Notification notification) {
        Context context = new Context();
        context.setVariable("name", notification.title());
        context.setVariable("scheduledDateTime", notification.getFormattedScheduledTime());
        context.setVariable("description", notification.description());

        return templateEngine.process("email/thymeleaf", context);
    }
}
