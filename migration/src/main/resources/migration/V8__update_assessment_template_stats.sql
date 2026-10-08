ALTER TABLE assessment_template_stats
    ADD COLUMN IF NOT EXISTS generation_failure_count INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS last_generation_failure_at TIMESTAMPTZ;