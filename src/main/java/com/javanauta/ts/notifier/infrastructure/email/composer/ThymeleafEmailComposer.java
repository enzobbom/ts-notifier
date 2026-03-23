package com.javanauta.ts.notifier.infrastructure.email.composer;

import com.javanauta.ts.notifier.domain.ports.email.EmailComposer;
import lombok.AllArgsConstructor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@AllArgsConstructor
public class ThymeleafEmailComposer implements EmailComposer {
    private final TemplateEngine templateEngine;

    public String composeEmail(String taskName, String taskScheduledTime, String taskDescription) {
        Context context = new Context();
        context.setVariable("name", taskName);
        context.setVariable("scheduledDateTime", taskScheduledTime);
        context.setVariable("description", taskDescription);

        return templateEngine.process("email/thymeleaf", context);
    }
}
