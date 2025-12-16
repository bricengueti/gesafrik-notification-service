package gesafrik.gesafrikNotification.DTO;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record NotificationMessage(
        String id,
        String type, // EMAIL, SMS, PUSH
        String recipient,
        String subject,
        String content,
        LocalDateTime timestamp,
        Map<String, Object> metadata
) {

    // Constructor with default values for id and timestamp
    public NotificationMessage {
        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (metadata == null) {
            metadata = Map.of();
        }
    }

    // Convenience constructors
    public NotificationMessage(String type, String recipient, String subject, String content) {
        this(null, type, recipient, subject, content, null, null);
    }

    public NotificationMessage(String type, String recipient, String subject, String content,
                               Map<String, Object> metadata) {
        this(null, type, recipient, subject, content, null, metadata);
    }

    // Utility methods for creating specific notification types
    public static NotificationMessage createEmail(String recipient, String subject, String content) {
        return new NotificationMessage("EMAIL", recipient, subject, content);
    }

    public static NotificationMessage createSms(String recipient, String content) {
        return new NotificationMessage("SMS", recipient, null, content);
    }

    public static NotificationMessage createPush(String recipient, String title, String body) {
        return new NotificationMessage("PUSH", recipient, title, body);
    }

    // Utility method to check notification type
    public boolean isEmail() {
        return "EMAIL".equalsIgnoreCase(type);
    }

    public boolean isSms() {
        return "SMS".equalsIgnoreCase(type);
    }

    public boolean isPush() {
        return "PUSH".equalsIgnoreCase(type);
    }

    // Method to add metadata
    public NotificationMessage withMetadata(String key, Object value) {
        Map<String, Object> newMetadata = new java.util.HashMap<>(metadata);
        newMetadata.put(key, value);
        return new NotificationMessage(id, type, recipient, subject, content, timestamp, newMetadata);
    }

    // Method to update content
    public NotificationMessage withContent(String newContent) {
        return new NotificationMessage(id, type, recipient, subject, newContent, timestamp, metadata);
    }
}