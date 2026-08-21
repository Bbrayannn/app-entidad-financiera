package com.entidadfinanciera.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Entidad Financiera")
                        .description("Prueba técnica: administración de clientes, productos financieros y transacciones.")
                        .version("1.0.0")
                        .contact(new Contact().name("Equipo Backend")));
    }
}