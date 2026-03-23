package com.javanauta.ts.notifier.infrastructure.config.email;

import com.javanauta.ts.notifier.domain.ports.email.EmailSender;
import com.javanauta.ts.notifier.infrastructure.email.sender.NoOpEmailSender;
import com.javanauta.ts.notifier.infrastructure.email.sender.SmtpEmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class EmailSenderConfig {

    private final JavaMailSender javaMailSender;

    @Bean
    @ConditionalOnExpression("'${spring.mail.username}'.length() > 0 && '${spring.mail.password}'.length() > 0")
    @Primary
    public EmailSender smtpEmailSender() {
        return new SmtpEmailSender(javaMailSender);
    }

    @Bean
    @ConditionalOnMissingBean(EmailSender.class)
    public EmailSender noOpEmailSender() {
        log.warn("SMTP server username and password could not be read. NoOpEmailSender will be used");
        return new NoOpEmailSender();
    }
}
