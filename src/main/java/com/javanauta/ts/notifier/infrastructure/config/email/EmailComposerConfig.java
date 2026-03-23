package com.javanauta.ts.notifier.infrastructure.config.email;

import com.javanauta.ts.notifier.domain.ports.email.EmailComposer;
import com.javanauta.ts.notifier.infrastructure.email.composer.ThymeleafEmailComposer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.thymeleaf.TemplateEngine;

@Configuration
@RequiredArgsConstructor
public class EmailComposerConfig {

    @Bean
    @Primary
    public EmailComposer thymeleafEmailComposer() {
        return new ThymeleafEmailComposer(new TemplateEngine());
    }
}
