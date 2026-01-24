package gesafrik.gesafrikNotification.service;

import gesafrik.gesafrikNotification.DTO.NotificationMessage;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ResourceLoader resourceLoader;

    public void sendEmail(NotificationMessage notification) {
        try {
            // Charger le fichier HTML
            Resource resource = resourceLoader.getResource("classpath:templates/register-society-email.html");
            String htmlContent = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            // Remplacer les variables dynamiques
            htmlContent = htmlContent
                    .replace("{{societyName}}", (String) notification.metadata().get("societyName"))
                    .replace("{{societyCode}}", (String) notification.metadata().get("societyCode"))
                    .replace("{{frontendLink}}", (String) notification.metadata().get("frontendLink"))
                    .replace("{{userName}}", (String) notification.metadata().get("userName"));

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(notification.recipient());
            helper.setSubject(notification.subject());
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(mimeMessage);

            log.info("HTML email envoyé avec succès à {}", notification.recipient());
        } catch (Exception e) {
            log.error("Échec d’envoi de l’email à {}: {}", notification.recipient(), e.getMessage(), e);
        }
    }
}
