package com.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notificationservice.event.UserEvent;
import com.notificationservice.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public NotificationListener(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void handleUserEvent(String message) {
        try {
            UserEvent event = objectMapper.readValue(message, UserEvent.class);
            notificationService.sendNotification(event.getEmail(), event.getOperation());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
