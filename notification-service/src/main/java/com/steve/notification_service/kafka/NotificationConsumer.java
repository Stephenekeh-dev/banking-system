package com.steve.notification_service.kafka;

import com.steve.notification_service.dto.NotificationRequest;
import com.steve.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void consume(NotificationRequest request) {

        notificationService.sendNotification(request);
    }
}
