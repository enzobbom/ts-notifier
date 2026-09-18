package com.javanauta.ts.notifier.adapters.out.email.sender;

import com.javanauta.ts.notifier.adapters.out.email.data.EmailMessage;
import com.javanauta.ts.notifier.adapters.out.email.exception.EmailException;
import com.javanauta.ts.notifier.adapters.out.email.exception.enums.EmailExceptionCode;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.net.ConnectException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmtpEmailSenderTest {
    @Mock
    private JavaMailSender javaMailSender;

    private SmtpEmailSender emailSender;

    @BeforeEach
    void setUp() {
        emailSender = new SmtpEmailSender(javaMailSender);
    }

    @Test
    void send_shouldSendEmailSuccessfully() {
        MimeMessage mimeMessage = new MimeMessage(
                Session.getInstance(new Properties())
        );

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        EmailMessage message = emailMessage();

        emailSender.send(message);

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    void send_shouldThrowInternalErrorWhenMessageConstructionFails() {
        MimeMessage mimeMessage = new MimeMessage(
                Session.getInstance(new Properties())
        );

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        EmailMessage message = EmailMessage.builder()
                .sender("test@example.com")
                .senderName("Javanauta")
                .recipient("email@email@email")
                .subject("Task Notification")
                .body("<p>Test</p>")
                .build();

        assertThatThrownBy(() -> emailSender.send(message))
                .isInstanceOfSatisfying(EmailException.class, exception ->
                        assertThat(exception.getCode())
                                .isEqualTo(EmailExceptionCode.INTERNAL_ERROR));

        verify(javaMailSender, never()).send(mimeMessage);
    }

    @Test
    void send_shouldThrowInfrastructureUnavailableWhenSendingFailsDueToConnectionRelatedExceptions() {
        MimeMessage mimeMessage = new MimeMessage(
                Session.getInstance(new Properties())
        );

        when(javaMailSender.createMimeMessage())
                .thenReturn(mimeMessage);

        ConnectException cause =
                new ConnectException("Connection refused");

        doThrow(new MailSendException("Could not send email", cause))
                .when(javaMailSender)
                .send(mimeMessage);

        assertThatThrownBy(() -> emailSender.send(emailMessage()))
                .isInstanceOfSatisfying(EmailException.class, exception ->
                        assertThat(exception.getCode())
                                .isEqualTo(EmailExceptionCode.INFRASTRUCTURE_UNAVAILABLE));
    }

    @Test
    void send_shouldThrowInternalErrorWhenSendingFailsDueToNonConnectionRelatedExceptions() {
        MimeMessage mimeMessage = new MimeMessage(
                Session.getInstance(new Properties())
        );

        when(javaMailSender.createMimeMessage())
                .thenReturn(mimeMessage);

        AuthenticationFailedException cause =
                new AuthenticationFailedException("Authentication failed");

        doThrow(new MailSendException("Could not send email", cause))
                .when(javaMailSender)
                .send(mimeMessage);

        assertThatThrownBy(() -> emailSender.send(emailMessage()))
                .isInstanceOfSatisfying(EmailException.class, exception ->
                        assertThat(exception.getCode())
                                .isEqualTo(EmailExceptionCode.INTERNAL_ERROR));
    }

    private EmailMessage emailMessage() {
        return EmailMessage.builder()
                .sender("test@example.com")
                .senderName("Javanauta")
                .recipient("recipient@example.com")
                .subject("Task Notification")
                .body("<p>Test</p>")
                .build();
    }
}