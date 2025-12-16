package gesafrik.gesafrikNotification.controller;


import gesafrik.gesafrikNotification.DTO.NotificationMessage;
import gesafrik.gesafrikNotification.service.NotificationProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationProducer notificationProducer;

    /**
     * Send a generic notification
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> sendNotification(@RequestBody NotificationMessage notification) {
        try {
            log.info("Received notification request: Type={}, Recipient={}",
                    notification.type(), notification.recipient());

            notificationProducer.sendNotification(notification);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Notification sent successfully",
                    "notificationId", notification.id(),
                    "type", notification.type()
            ));
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "status", "error",
                            "message", "Error sending notification: " + e.getMessage()
                    ));
        }
    }

    /**
     * Send email notification
     */
    @PostMapping("/email")
    public ResponseEntity<Map<String, Object>> sendEmail(
            @RequestParam String recipient,
            @RequestParam String subject,
            @RequestParam String content,
            @RequestParam(required = false) Map<String, Object> metadata) {

        try {
            log.info("Received email notification request: Recipient={}, Subject={}", recipient, subject);

            NotificationMessage notification;
            if (metadata != null && !metadata.isEmpty()) {
                notification =  NotificationMessage.createEmail( recipient, subject, content);
            } else {
                notification = NotificationMessage.createEmail(recipient, subject, content);
            }

            notificationProducer.sendNotification(notification);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Email notification sent successfully",
                    "notificationId", notification.id(),
                    "recipient", recipient
            ));
        } catch (Exception e) {
            log.error("Error sending email notification: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "status", "error",
                            "message", "Error sending email notification: " + e.getMessage()
                    ));
        }
    }

    /**
     * Send SMS notification
     */
    @PostMapping("/sms")
    public ResponseEntity<Map<String, Object>> sendSms(
            @RequestParam String recipient,
            @RequestParam String content,
            @RequestParam(required = false) Map<String, Object> metadata) {

        try {
            log.info("Received SMS notification request: Recipient={}", recipient);

            NotificationMessage notification;
            if (metadata != null && !metadata.isEmpty()) {
                notification = new NotificationMessage("SMS", recipient, null, content, metadata);
            } else {
                notification = NotificationMessage.createSms(recipient, content);
            }

            notificationProducer.sendNotification(notification);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "SMS notification sent successfully",
                    "notificationId", notification.id(),
                    "recipient", recipient
            ));
        } catch (Exception e) {
            log.error("Error sending SMS notification: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "status", "error",
                            "message", "Error sending SMS notification: " + e.getMessage()
                    ));
        }
    }

    /**
     * Send push notification
     */
    @PostMapping("/push")
    public ResponseEntity<Map<String, Object>> sendPush(
            @RequestParam String recipient,
            @RequestParam String title,
            @RequestParam String body,
            @RequestParam(required = false) Map<String, Object> metadata) {

        try {
            log.info("Received push notification request: Recipient={}, Title={}", recipient, title);

            NotificationMessage notification;
            if (metadata != null && !metadata.isEmpty()) {
                notification = new NotificationMessage("PUSH", recipient, title, body, metadata);
            } else {
                notification = NotificationMessage.createPush(recipient, title, body);
            }

            notificationProducer.sendNotification(notification);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Push notification sent successfully",
                    "notificationId", notification.id(),
                    "recipient", recipient
            ));
        } catch (Exception e) {
            log.error("Error sending push notification: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "status", "error",
                            "message", "Error sending push notification: " + e.getMessage()
                    ));
        }
    }

    /**
     * Send notification with JSON body for more complex scenarios
     */
    @PostMapping("/custom")
    public ResponseEntity<Map<String, Object>> sendCustomNotification(
            @RequestParam String type,
            @RequestParam String recipient,
            @RequestParam(required = false) String subject,
            @RequestParam String content,
            @RequestBody(required = false) Map<String, Object> metadata) {

        try {
            log.info("Received custom notification request: Type={}, Recipient={}", type, recipient);

            NotificationMessage notification = new NotificationMessage(type, recipient, subject, content, metadata);
            notificationProducer.sendNotification(notification);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Custom notification sent successfully",
                    "notificationId", notification.id(),
                    "type", type,
                    "recipient", recipient
            ));
        } catch (Exception e) {
            log.error("Error sending custom notification: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "status", "error",
                            "message", "Error sending custom notification: " + e.getMessage()
                    ));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        log.info("Health check endpoint called");
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "service", "gesafrik-notification",
                "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }

    /**
     * Get service info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getServiceInfo() {
        return ResponseEntity.ok(Map.of(
                "name", "Gesafrik Notification Service",
                "version", "1.0.0",
                "description", "Kafka-based notification service for Gesafrik platform",
                "supportedTypes", new String[]{"EMAIL", "SMS", "PUSH"},
                "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}
