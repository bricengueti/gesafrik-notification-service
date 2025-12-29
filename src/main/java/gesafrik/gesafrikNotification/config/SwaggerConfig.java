package gesafrik.gesafrikNotification.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Gesafrik Notification Service API",
                version = "1.0",
                description = """
                    API de gestion des notifications pour Gesafrik.
                    
                    ## Fonctionnalités :
                    - Envoi de notifications par email
                    - Envoi de notifications SMS
                    - Gestion des templates de notification
                    - Suivi des statuts d'envoi
                    - Historique des notifications
                    
                    ## Environnements :
                    - **Développement** : http://localhost:8088
                    - **Production** : https://api.gesafrik.com
                    
                    ## Documentation additionnelle :
                    [Guide d'intégration des notifications](https://docs.gesafrik.com/notifications)
                    """,
                contact = @Contact(
                        name = "Équipe Support Gesafrik",
                        email = "support.notifications@gesafrik.com",
                        url = "https://support.gesafrik.com/notifications"
                ),
                license = @License(
                        name = "Licence API Gesafrik v2.1",
                        url = "https://www.gesafrik.com/terms/api-license"
                ),
                termsOfService = "https://www.gesafrik.com/terms/api"
        ),
        servers = {
                @Server(
                        url = "/api/notification",
                        description = "Server local avec base path"
                )
        },
        security = {
                @SecurityRequirement(name = "JWT Authentication")
        }
)
@SecurityScheme(
        name = "JWT Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = """
            Authentification par JWT (JSON Web Token).
            
            ## Comment obtenir un token :
            1. Authentifiez-vous via `/api/auth/login`
            2. Récupérez le token dans la réponse
            3. Ajoutez-le dans l'en-tête : `Authorization: Bearer <votre_token>`
            
            ## Durée de validité :
            - Tokens d'accès : 1 heure
            - Tokens de rafraîchissement : 7 jours
            
            ## Sécurité :
            - Les tokens sont signés avec RSA-256
            - Chaque token contient les permissions de l'utilisateur
            """,
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {

    // Configuration supplémentaire si nécessaire
    // @Bean
    // public OpenAPI customizeOpenAPI() {
    //     return new OpenAPI()
    //         .components(new Components())
    //         .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement()
    //                 .addList("JWT Authentication"));
    // }
}