package com.javanauta.ts.notifier.infrastructure.notification.config;

import com.javanauta.ts.notifier.business.EmailService;
import com.javanauta.ts.notifier.business.NoOpEmailService;
import com.javanauta.ts.notifier.business.SmtpEmailService;
import com.javanauta.ts.notifier.infrastructure.notification.JavaMailSenderNotifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class EmailServiceConfig {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Bean
    @ConditionalOnExpression("'${spring.mail.username}'.length() > 0 && '${spring.mail.password}'.length() > 0")
    @Primary
    public EmailService emailService() {
        JavaMailSenderNotifier javaMailSenderNotifier = new JavaMailSenderNotifier(javaMailSender);
        return new SmtpEmailService(javaMailSenderNotifier, templateEngine);
    }

    @Bean
    @ConditionalOnMissingBean(EmailService.class)
    public EmailService noOpEmailService() {
        log.warn("SMTP server username and password could not be read. NoOpEmailService will be used");
        return new NoOpEmailService();
    }
}
