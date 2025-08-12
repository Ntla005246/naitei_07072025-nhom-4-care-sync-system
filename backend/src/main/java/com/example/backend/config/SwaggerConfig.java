package com.example.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final AppProperties appProperties;

    @Bean
    public OpenAPI openAPI() {
        // Contact Information
        Contact contact = new Contact().name(appProperties.getSwagger().getContactName())
                .email(appProperties.getSwagger().getContactEmail())
                .url(appProperties.getSwagger().getContactUrl());

        // API Information
        Info info = new Info().title(appProperties.getSwagger().getTitle())
                .version(appProperties.getSwagger().getVersion())
                .description(appProperties.getSwagger().getDescription()).contact(contact);

        // Security Scheme for JWT
        final String securitySchemeName = "Bearer Authentication";
        SecurityScheme securityScheme = new SecurityScheme().name(securitySchemeName)
                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT");

        // Server Information
        Server localServer = new Server().url("http://localhost:8080")
                .description("Local Development Server");
        // You can add other servers (e.g., production) here
        // Server prodServer = new
        // Server().url("https://your-prod-url.com").description("Production Server");

        return new OpenAPI().info(info).servers(List.of(localServer)) // Add prodServer to the list
                                                                      // when needed
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)).components(
                        new Components().addSecuritySchemes(securitySchemeName, securityScheme));
    }
}
