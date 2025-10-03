package com.notificationservice.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNotification (String email, String operation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);

        if ("CREATE".equalsIgnoreCase(operation)) {
            message.setSubject("Аккаунт создан");
            message.setText("Здравствуйте! Ваш аккаунт на сайте был успешно создан.");
        } else if ("DELETE".equalsIgnoreCase(operation)) {
            message.setSubject("Аккаунт удалён");
            message.setText("Здравствуйте! Ваш аккаунт был удалён.");
        } else {
            throw new IllegalArgumentException("Unknown operation: " + operation);
        }

        mailSender.send(message);
    }
}
