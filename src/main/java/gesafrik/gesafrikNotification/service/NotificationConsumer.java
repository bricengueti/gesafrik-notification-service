package gesafrik.gesafrikNotification.service;

import gesafrik.gesafrikNotification.DTO.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    @KafkaListener(
            topics = "${app.kafka.topics.notification:gesafrik-notifications}",
            groupId = "${spring.kafka.consumer.group-id:gesafrik-notification-group}"
    )
    public void consumeNotification(@Payload NotificationMessage notification) {
        log.info("Received notification: ID={}, Type={}, Recipient={}",
                notification.id(), notification.type(), notification.recipient());

        try {
            processNotification(notification);
            log.info("Notification processed successfully: ID={}", notification.id());
        } catch (Exception e) {
            log.error("Error processing notification: ID={}, Error={}",
                    notification.id(), e.getMessage(), e);
            // Implement dead letter queue or retry logic here
        }
    }

    @KafkaListener(
            topics = "${app.kafka.topics.email:gesafrik-email-notifications}",
            groupId = "${spring.kafka.consumer.group-id:gesafrik-notification-group}"
    )
    public void consumeEmailNotification(@Payload NotificationMessage notification) {
        log.info("Processing EMAIL notification: ID={}", notification.id());
        processEmail(notification);
    }

    @KafkaListener(
            topics = "${app.kafka.topics.sms:gesafrik-sms-notifications}",
            groupId = "${spring.kafka.consumer.group-id:gesafrik-notification-group}"
    )
    public void consumeSmsNotification(@Payload NotificationMessage notification) {
        log.info("Processing SMS notification: ID={}", notification.id());
        processSms(notification);
    }

    @KafkaListener(
            topics = "${app.kafka.topics.push:gesafrik-push-notifications}",
            groupId = "${spring.kafka.consumer.group-id:gesafrik-notification-group}"
    )
    public void consumePushNotification(@Payload NotificationMessage notification) {
        log.info("Processing PUSH notification: ID={}", notification.id());
        processPush(notification);
    }

    private void processNotification(NotificationMessage notification) {
        switch (notification.type().toUpperCase()) {
            case "EMAIL" -> processEmail(notification);
            case "SMS" -> processSms(notification);
            case "PUSH" -> processPush(notification);
            default -> log.warn("Unknown notification type: {}", notification.type());
        }
    }

    private void processEmail(NotificationMessage notification) {
        // Implement email sending logic here
        log.info("Sending EMAIL to: {}, Subject: {}, Content: {}",
                notification.recipient(),
                notification.subject(),
                notification.content());

        // TODO: Integrate with your email service (SMTP, SendGrid, etc.)
    }

    private void processSms(NotificationMessage notification) {
        // Implement SMS sending logic here
        log.info("Sending SMS to: {}, Content: {}",
                notification.recipient(),
                notification.content());

        // TODO: Integrate with your SMS gateway
    }

    private void processPush(NotificationMessage notification) {
        // Implement push notification logic here
        log.info("Sending PUSH to: {}, Title: {}, Body: {}",
                notification.recipient(),
                notification.subject(),
                notification.content());

        // TODO: Integrate with your push notification service (FCM, APNS, etc.)
    }
}