package gesafrik.gesafrikNotification.service;

import gesafrik.gesafrikNotification.DTO.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(NotificationMessage notification) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(notification.recipient());
            message.setSubject(notification.subject() != null ? notification.subject() : "Notification");
            message.setText(notification.content());

            mailSender.send(message);

            log.info("Email sent successfully to {}", notification.recipient());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", notification.recipient(), e.getMessage(), e);
        }
    }
}
