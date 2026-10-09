package com.maboy.applicantmarket.commons.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI applicantMarketOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Applicant Market API")
                        .description("Цифровая платформа-агрегатор ИТ-вакансий с верифицированным профилем ФСП")
                        .version("0.0.1"))
                .components(new Components()
                        .addSecuritySchemes("sessionCookie", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("sessionId")
                                .description("Сессионная cookie, выдаётся при /api/auth/login и /api/auth/confirm-email")));
    }
}