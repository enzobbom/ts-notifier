package com.javanauta.ts.notifier.business;

import com.javanauta.ts.notifier.business.dto.TaskDTO;

public interface EmailService {
    void sendEmail(TaskDTO taskDTO);
}
