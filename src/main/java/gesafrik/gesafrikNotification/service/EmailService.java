package gesafrik.gesafrikNotification.service;

import gesafrik.gesafrikNotification.DTO.NotificationMessage;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
            String htmlContent = "";
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            switch (notification.content()) {
                case "REGISTER_SOCIETY" -> {
                    htmlContent = loadTemplate("classpath:templates/register-society-email.html")
                            .replace("{{societyName}}", (String) notification.metadata().get("societyName"))
                            .replace("{{societyCode}}", (String) notification.metadata().get("societyCode"))
                            .replace("{{frontendLink}}", (String) notification.metadata().get("frontendLink"))
                            .replace("{{userName}}", (String) notification.metadata().get("userName"));

                    helper.setTo(notification.recipient());
                    helper.setSubject(notification.subject());
                    helper.setText(htmlContent, true);

                    // 🔹 Ajouter une pièce jointe (PDF placé dans resources/attachments/)
                    Resource logoResource = resourceLoader.getResource("classpath:attachments/logo.png");
                    helper.addInline("logoImage", logoResource);
                    helper.addAttachment("Logo.png", logoResource);

                    mailSender.send(mimeMessage);
                    log.info("Email REGISTER_SOCIETY avec pièce jointe envoyé à {}", notification.recipient());
                    return; // on sort ici car déjà envoyé
                }

                case "REGISTER_EMPLOYEE" -> {
                    htmlContent = loadTemplate("classpath:templates/register-employee-email.html")
                            .replace("{{employeeName}}", (String) notification.metadata().get("employeeName"))
                            .replace("{{societyName}}", (String) notification.metadata().get("societyName"))
                            .replace("{{frontendLink}}", (String) notification.metadata().get("frontendLink"));
                }

                case "RESET_PASSWORD" -> {
                    htmlContent = loadTemplate("classpath:templates/reset-password-email.html")
                            .replace("{{frontendLink}}", (String) notification.metadata().get("frontendLink"))
                            .replace("{{userId}}", (String) notification.metadata().get("userId"));
                }

                default -> {
                    htmlContent = notification.content(); // fallback texte brut
                }
            }

            // 🔹 Envoi pour les cas sans pièce jointe
            helper.setTo(notification.recipient());
            helper.setSubject(notification.subject());
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("HTML email envoyé avec succès à {}", notification.recipient());

        } catch (Exception e) {
            log.error("Échec d’envoi de l’email à {}: {}", notification.recipient(), e.getMessage(), e);
        }
    }

    private String loadTemplate(String path) throws IOException {
        Resource resource = resourceLoader.getResource(path);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
