package com.maboy.applicantmarket.assessment.config;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "assessment")
@Getter
@Setter
public class AssessmentProperties {

    private Session session = new Session();
    private Generation generation = new Generation();
    private Grading grading = new Grading();

    void validate() {
        int sum = generation.getDistribution().getBelowClaimed()
                  + generation.getDistribution().getClaimed()
                  + generation.getDistribution().getAboveClaimed()
                  + generation.getDistribution().getRandom();
        if (sum != generation.getTotalItems()) {
            throw new IllegalStateException(
                "assessment.generation.distribution sum (" + sum +
                ") must equal assessment.generation.total-items (" +
                generation.getTotalItems() + ")");
        }
        if (grading.getThresholds().getPass() > grading.getThresholds().getPromote()) {
            throw new IllegalStateException(
                "assessment.grading.thresholds.pass must be <= promote");
        }
        if (session.getExpiresIn().isNegative() || session.getExpiresIn().isZero()) {
            throw new IllegalStateException(
                "assessment.session.expires-in must be positive");
        }
        if (generation.getMaxAttemptsPerTemplate() < 1) {
            throw new IllegalStateException(
                "assessment.generation.max-attempts-per-template must be >= 1");
        }
        if (generation.getAutoDeactivateThreshold() < 5) {
            throw new IllegalStateException(
                "assessment.generation.auto-deactivate-threshold must be >= 5");
        }
    }


    @Getter
    @Setter
    public static class Session {

        private Duration expiresIn = Duration.ofHours(2);
        private Duration gracePeriod = Duration.ZERO;
    }

    @Getter
    @Setter
    public static class Generation {

        private int totalItems = 8;
        private Distribution distribution = new Distribution();
        private String rendererVersion = "v1";
        private int maxAttemptsPerTemplate = 5;
        private int autoDeactivateThreshold = 5;

        @Getter
        @Setter
        public static class Distribution {

            private int belowClaimed = 2;
            private int claimed = 3;
            private int aboveClaimed = 2;
            private int random = 1;
        }
    }

    @Getter
    @Setter
    public static class Grading {

        private Map<String, Integer> gradeToDifficulty = Map.of(
            "JUNIOR", 1, "MIDDLE", 3, "SENIOR", 4, "LEAD", 5);
        private Thresholds thresholds = new Thresholds();

        @Getter
        @Setter
        public static class Thresholds {

            private double pass = 0.5;
            private double promote = 0.8;
        }
    }
}