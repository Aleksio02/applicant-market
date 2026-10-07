package com.maboy.applicantmarket.applicant.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "applicant.grade-change")
@Getter
@Setter
public class GradeCooldownProperties {
    private Duration cooldown = Duration.ofDays(90);
}