ALTER TABLE assessment_template_stats
    ADD CONSTRAINT chk_assessment_stats_generation_failures
        CHECK (generation_failure_count >= 0);