package com.steve.notification_service.service;

import com.steve.notification_service.dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void sendNotification(NotificationRequest request) {
        // In a real app: integrate with email/SMS provider
        log.info("📩 Sending notification to {} with message: {}",
                request.getRecipient(), request.getMessage());
    }
}