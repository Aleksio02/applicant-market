package com.maboy.applicantmarket.commons.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final List<String> TAG_ORDER = List.of(
            "Auth"
    );

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

    /**
     * Переставляет теги в порядке из TAG_ORDER.
     * Все прочие теги уходят в конец, отсортированные по алфавиту.
     */
    @Bean
    public OpenApiCustomizer tagOrderCustomizer() {
        return openApi -> {
            if (openApi.getTags() == null || openApi.getTags().isEmpty()) {
                return;
            }
            List<Tag> ordered = new ArrayList<>();
            for (String name : TAG_ORDER) {
                openApi.getTags().stream()
                        .filter(t -> name.equals(t.getName()))
                        .findFirst()
                        .ifPresent(ordered::add);
            }
            openApi.getTags().stream()
                    .filter(t -> !TAG_ORDER.contains(t.getName()))
                    .sorted(Comparator.comparing(Tag::getName))
                    .forEach(ordered::add);
            openApi.setTags(ordered);
        };
    }
}