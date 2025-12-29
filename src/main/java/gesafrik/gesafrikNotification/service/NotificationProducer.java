package gesafrik.gesafrikNotification.service;


import gesafrik.gesafrikNotification.DTO.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class NotificationProducer {


    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);

    @Value("${app.kafka.topics.notification:gesafrik-notifications}")
    private String notificationTopic;

    @Value("${app.kafka.topics.email:gesafrik-email-notifications}")
    private String emailTopic;

    @Value("${app.kafka.topics.sms:gesafrik-sms-notifications}")
    private String smsTopic;

    @Value("${app.kafka.topics.push:gesafrik-push-notifications}")
    private String pushTopic;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendNotification(NotificationMessage notification) {
        try {
            String topic = determineTopic(notification.type());

            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(topic, notification.id(), notification);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Notification sent successfully: ID={}, Topic={}, Partition={}, Offset={}",
                            notification.id(), topic,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send notification: ID={}, Error={}",
                            notification.id(), ex.getMessage());
                    // Here you can implement retry logic or dead letter queue
                }
            });

        } catch (Exception e) {
            log.error("Error while sending notification: {}", e.getMessage(), e);
        }
    }

    private String determineTopic(String notificationType) {
        if (notificationType == null) {
            return notificationTopic;
        }

        return switch (notificationType.toUpperCase()) {
            case "EMAIL" -> emailTopic;
            case "SMS" -> smsTopic;
            case "PUSH" -> pushTopic;
            default -> notificationTopic;
        };
    }

    public void sendToSpecificTopic(String topic, NotificationMessage notification) {
        try {
            kafkaTemplate.send(topic, notification.id(), notification)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Message sent to topic {}: ID={}", topic, notification.id());
                        } else {
                            log.error("Failed to send to topic {}: {}", topic, ex.getMessage());
                        }
                    });

        } catch (Exception e) {
            log.error("Error sending to topic {}: {}", topic, e.getMessage());
        }
    }

    // Convenience methods for common notification types
    public void sendEmail(String recipient, String subject, String content) {
        NotificationMessage notification = NotificationMessage.createEmail(recipient, subject, content);
        sendNotification(notification);
    }

    public void sendSms(String recipient, String content) {
        NotificationMessage notification = NotificationMessage.createSms(recipient, content);
        sendNotification(notification);
    }

    public void sendPush(String recipient, String title, String body) {
        NotificationMessage notification = NotificationMessage.createPush(recipient, title, body);
        sendNotification(notification);
    }


    // Dans NotificationProducer.java
    public void producerEmailNotification(NotificationMessage notification) {
        try {
            // Forcer le type à EMAIL si ce n’est pas déjà le cas
            NotificationMessage emailNotification = notification.isEmail()
                    ? notification
                    : NotificationMessage.createEmail(notification.recipient(), notification.subject(), notification.content());

            kafkaTemplate.send(emailTopic, emailNotification.id(), emailNotification)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Email notification sent producer successfully: ID={}, Recipient={}, Partition={}, Offset={}",
                                    emailNotification.id(),
                                    emailNotification.recipient(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        } else {
                            log.error("Failed to send email notification: ID={}, Recipient={}, Error={}",
                                    emailNotification.id(),
                                    emailNotification.recipient(),
                                    ex.getMessage(), ex);
                            // TODO: retry logic or dead-letter topic
                        }
                    });

        } catch (Exception e) {
            log.error("Error while producing email notification: {}", e.getMessage(), e);
            throw e; // pour que le contrôleur capture l’exception
        }
    }

}