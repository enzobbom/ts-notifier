package com.javanauta.ts.notifier.presentation.controller;

import com.javanauta.ts.notifier.presentation.mapper.NotificationMapper;
import com.javanauta.ts.notifier.application.usecase.NotificationService;
import com.javanauta.ts.notifier.presentation.dto.NotifyTaskRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @PostMapping
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody NotifyTaskRequestDTO notifyTaskRequestDTO) {
        notificationService.notify(notificationMapper.toDomain(notifyTaskRequestDTO));
        return ResponseEntity.ok().build();
    }
}
