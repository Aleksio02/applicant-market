-- =============================================================
-- assessment_templates
-- Parameterized task templates. difficulty is the calibrated
-- level of the template, not of the specific generated instance.
-- parameter_spec describes ranges/types of placeholders.
-- answer_spec describes how the correct answer is computed.
-- =============================================================
CREATE TABLE IF NOT EXISTS assessment_templates
(
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    code               VARCHAR(64)  NOT NULL UNIQUE,
    skill_id           UUID         NOT NULL,
    topic              VARCHAR(64)  NOT NULL,
    type               VARCHAR(32)  NOT NULL,
    difficulty         SMALLINT     NOT NULL,
    body_template      TEXT         NOT NULL,
    parameter_spec     JSONB        NOT NULL,
    answer_spec        JSONB        NOT NULL,
    explanation_template TEXT,
    is_active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    version            INTEGER      NOT NULL DEFAULT 0,

    CONSTRAINT chk_assessment_templates_code
        CHECK (code ~ '^[A-Z0-9_]+$'),
    CONSTRAINT chk_assessment_templates_type
        CHECK (type IN ('MCQ','OUTPUT_PREDICT','BUG_FIND')),
    CONSTRAINT chk_assessment_templates_difficulty
        CHECK (difficulty BETWEEN 1 AND 5)
);

CREATE INDEX IF NOT EXISTS idx_assessment_templates_pick
    ON assessment_templates (skill_id, difficulty, is_active);
CREATE INDEX IF NOT EXISTS idx_assessment_templates_topic
    ON assessment_templates (topic, is_active);

-- =============================================================
-- assessment_template_stats
-- Calibration metrics. Updated asynchronously by a batch job
-- or manually. Kept separate from templates to avoid contention.
-- =============================================================
CREATE TABLE IF NOT EXISTS assessment_template_stats
(
    template_id          UUID         PRIMARY KEY
        CONSTRAINT fk_assessment_stats_template
            REFERENCES assessment_templates (id) ON DELETE CASCADE,
    served_count         INTEGER      NOT NULL DEFAULT 0,
    correct_count        INTEGER      NOT NULL DEFAULT 0,
    correct_rate         NUMERIC(5,4),
    discrimination_index NUMERIC(5,4),
    last_calculated_at   TIMESTAMPTZ,
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_assessment_stats_served
        CHECK (served_count >= 0),
    CONSTRAINT chk_assessment_stats_correct
        CHECK (correct_count >= 0 AND correct_count <= served_count),
    CONSTRAINT chk_assessment_stats_correct_rate
        CHECK (correct_rate IS NULL OR (correct_rate BETWEEN 0 AND 1)),
    CONSTRAINT chk_assessment_stats_discrimination
        CHECK (discrimination_index IS NULL OR (discrimination_index BETWEEN -1 AND 1))
);

-- =============================================================
-- assessment_sessions
-- One active session per (applicant, skill). Partial unique index
-- enforces this at the DB level.
-- =============================================================
CREATE TABLE IF NOT EXISTS assessment_sessions
(
    id                UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    applicant_id      UUID         NOT NULL,
    skill_id          UUID         NOT NULL,
    claimed_grade_id  UUID         NOT NULL,
    status            VARCHAR(32)  NOT NULL,
    result_grade_id   UUID,
    score             NUMERIC(5,4),
    started_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    completed_at      TIMESTAMPTZ,
    expires_at        TIMESTAMPTZ  NOT NULL,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    version           INTEGER      NOT NULL DEFAULT 0,

    CONSTRAINT chk_assessment_sessions_status
        CHECK (status IN ('SURVEY','IN_PROGRESS','COMPLETED','FAILED','EXPIRED','CANCELLED')),
    CONSTRAINT chk_assessment_sessions_score
        CHECK (score IS NULL OR (score BETWEEN 0 AND 1)),
    CONSTRAINT chk_assessment_sessions_completed
        CHECK ((status IN ('COMPLETED','FAILED') AND completed_at IS NOT NULL)
            OR (status NOT IN ('COMPLETED','FAILED') AND completed_at IS NULL)),
    CONSTRAINT chk_assessment_sessions_result
        CHECK ((status = 'COMPLETED' AND result_grade_id IS NOT NULL)
            OR (status <> 'COMPLETED' AND result_grade_id IS NULL))
);

-- One active session per applicant+skill.
CREATE UNIQUE INDEX IF NOT EXISTS uq_assessment_sessions_active
    ON assessment_sessions (applicant_id, skill_id)
    WHERE status IN ('SURVEY','IN_PROGRESS');

CREATE INDEX IF NOT EXISTS idx_assessment_sessions_applicant
    ON assessment_sessions (applicant_id, skill_id);
CREATE INDEX IF NOT EXISTS idx_assessment_sessions_status_expires
    ON assessment_sessions (status, expires_at);
CREATE INDEX IF NOT EXISTS idx_assessment_sessions_history
    ON assessment_sessions (applicant_id, started_at DESC);

-- =============================================================
-- assessment_items
-- Generated task instances inside a session. All fields needed
-- for evaluation and audit are copied here, so the item remains
-- valid even if the template is later deactivated or modified.
-- =============================================================
CREATE TABLE IF NOT EXISTS assessment_items
(
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id      UUID         NOT NULL
        CONSTRAINT fk_assessment_items_session
            REFERENCES assessment_sessions (id) ON DELETE CASCADE,
    template_id     UUID         NOT NULL,
    template_code   VARCHAR(64)  NOT NULL,
    position        INTEGER      NOT NULL,
    type            VARCHAR(32)  NOT NULL,
    topic           VARCHAR(64)  NOT NULL,
    difficulty      SMALLINT     NOT NULL,
    body            TEXT         NOT NULL,
    correct_answer  JSONB        NOT NULL,
    parameters      JSONB        NOT NULL,
    generation_meta JSONB        NOT NULL,
    points          SMALLINT     NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_assessment_items_position
        UNIQUE (session_id, position),
    CONSTRAINT chk_assessment_items_type
        CHECK (type IN ('MCQ','OUTPUT_PREDICT','BUG_FIND')),
    CONSTRAINT chk_assessment_items_difficulty
        CHECK (difficulty BETWEEN 1 AND 5),
    CONSTRAINT chk_assessment_items_position
        CHECK (position > 0),
    CONSTRAINT chk_assessment_items_points
        CHECK (points > 0)
);

CREATE INDEX IF NOT EXISTS idx_assessment_items_session
    ON assessment_items (session_id, position);
CREATE INDEX IF NOT EXISTS idx_assessment_items_template
    ON assessment_items (template_id);

-- =============================================================
-- assessment_answers
-- One answer per item.
-- =============================================================
CREATE TABLE IF NOT EXISTS assessment_answers
(
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id       UUID         NOT NULL UNIQUE
        CONSTRAINT fk_assessment_answers_item
            REFERENCES assessment_items (id) ON DELETE CASCADE,
    answer_json   JSONB        NOT NULL,
    is_correct    BOOLEAN      NOT NULL,
    score         SMALLINT     NOT NULL,
    answered_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    evaluated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_assessment_answers_score
        CHECK (score >= 0)
);

CREATE INDEX IF NOT EXISTS idx_assessment_answers_item
    ON assessment_answers (item_id);

-- =============================================================
-- assessment_survey_answers
-- Optional survey at session start. Reserved for future use
-- (e.g. self-assessment questions). For MVP may stay empty.
-- =============================================================
CREATE TABLE IF NOT EXISTS assessment_survey_answers
(
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id    UUID         NOT NULL
        CONSTRAINT fk_assessment_survey_session
            REFERENCES assessment_sessions (id) ON DELETE CASCADE,
    question_code VARCHAR(64)  NOT NULL,
    answer_json   JSONB        NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_assessment_survey_session_question
        UNIQUE (session_id, question_code)
);

CREATE INDEX IF NOT EXISTS idx_assessment_survey_session
    ON assessment_survey_answers (session_id);