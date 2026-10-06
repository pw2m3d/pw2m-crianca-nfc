package br.com.pw2m.nfc.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pw2mOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("PW2M Criança NFC API")
                                .description(
                                        """
                                        API do sistema PW2M Criança NFC.

                                        Permite:
                                        - cadastro de responsáveis;
                                        - autenticação JWT;
                                        - cadastro de crianças;
                                        - informações médicas;
                                        - contatos de emergência;
                                        - gerenciamento NFC;
                                        - consulta pública da ficha de emergência.
                                        """
                                )
                                .version("1.0.0")
                                .contact(
                                        new Contact()
                                                .name("PW2M 3D")
                                )
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .name("bearerAuth")
                                                .type(
                                                        SecurityScheme.Type.HTTP
                                                )
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }
}