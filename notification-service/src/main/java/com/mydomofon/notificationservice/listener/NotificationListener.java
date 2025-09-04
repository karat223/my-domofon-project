package com.mydomofon.notificationservice.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    @KafkaListener(topics = "payment_topic", groupId = "notification_group")
    public void listen(String message) {
        System.out.println("RECEIVED MESSAGE: " + message);
        System.out.println("-> Sending push notification to the user...");
        // Здесь в реальном проекте была бы логика отправки push-уведомления
    }
}
