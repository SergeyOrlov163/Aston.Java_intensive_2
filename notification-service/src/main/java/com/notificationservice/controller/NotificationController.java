package com.notificationservice.controller;

import com.notificationservice.dto.NotificationRequestDto;
import com.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification Management", description = "Операции с уведомлениями")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    @Operation(summary = "Отправить уведомление", description = "Отправляет уведомление на указанный email.")
    @ApiResponse(responseCode = "200", description = "Уведомление отправлено")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<Void> sendNotification(@Valid @RequestBody NotificationRequestDto requestDto) {
        notificationService.sendNotification(requestDto.getEmail(), requestDto.getOperation());
        return ResponseEntity.ok().build();
    }
}
