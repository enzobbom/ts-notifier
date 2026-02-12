package com.javanauta.ts.notifier.business;

import com.javanauta.ts.notifier.business.dto.TaskDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoOpEmailService implements EmailService {
    public void sendEmail(TaskDTO taskDTO) {
        log.debug("Notification of Task '{}' was skipped", taskDTO.getName());
    }
}
