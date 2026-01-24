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

                    // Ajouter le logo en ligne et en pièce jointe
                    Resource logoResource = resourceLoader.getResource("classpath:attachments/logo.png");
                    helper.addInline("logoImage", logoResource);
                    helper.addAttachment("Logo.png", logoResource);
                }
                case "REGISTER_EMPLOYEE" -> {
                    htmlContent = loadTemplate("classpath:templates/register-employee-email.html")
                            .replace("{{employeeName}}", (String) notification.metadata().get("employeeName"))
                            .replace("{{societyName}}", (String) notification.metadata().get("societyName"))
                            .replace("{{societyCode}}", (String) notification.metadata().get("societyCode"))
                            .replace("{{frontendLink}}", (String) notification.metadata().get("frontendLink"))
                            .replace("{{email}}", (String) notification.metadata().get("email"));

                    // Optionnel : ajouter le logo si disponible
                    try {
                        Resource logoResource = resourceLoader.getResource("classpath:attachments/logo.png");
                        if (logoResource.exists()) {
                            helper.addInline("logoImage", logoResource);
                        }
                    } catch (Exception e) {
                        log.warn("Logo non trouvé pour REGISTER_EMPLOYEE");
                    }
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

            // Envoi pour tous les cas (une seule fois)
            helper.setTo(notification.recipient());
            helper.setSubject(notification.subject());
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("HTML email envoyé avec succès à {} (type: {})", notification.recipient(), notification.content());

        } catch (Exception e) {
            log.error("Échec d'envoi de l'email à {}: {}", notification.recipient(), e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }

    private String loadTemplate(String path) throws IOException {
        Resource resource = resourceLoader.getResource(path);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}