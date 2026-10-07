-- =============================================================
-- skills
-- Reference data used by applicant, and later by vacancy, assessment.
-- =============================================================
CREATE TABLE IF NOT EXISTS skills
(
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    code       VARCHAR(64)  NOT NULL UNIQUE,
    name       VARCHAR(255) NOT NULL,
    category   VARCHAR(32)  NOT NULL,
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    sort_order INT          NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_skills_code     CHECK (code ~ '^[A-Z0-9_]+$'),
    CONSTRAINT chk_skills_category CHECK (category IN
        ('BACKEND','FRONTEND','MOBILE','DEVOPS','DATA','QA','TOOLING','OTHER'))
);

CREATE INDEX IF NOT EXISTS idx_skills_category    ON skills (category, is_active);
CREATE INDEX IF NOT EXISTS idx_skills_active_sort ON skills (is_active, sort_order);

-- =============================================================
-- categories
-- Materialized (specialization, grade) pair. Stable id for filters
-- and projections in matching.
-- =============================================================
CREATE TABLE IF NOT EXISTS categories
(
    id                UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    specialization_id UUID         NOT NULL CONSTRAINT fk_categories_specialization REFERENCES specializations(id),
    grade_id          UUID         NOT NULL CONSTRAINT fk_categories_grade REFERENCES grades(id),
    code              VARCHAR(100) NOT NULL UNIQUE,
    name              VARCHAR(255) NOT NULL,
    is_active         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_categories_spec_grade
    UNIQUE (specialization_id, grade_id)
);

CREATE INDEX IF NOT EXISTS idx_categories_specialization ON categories (specialization_id, is_active);
CREATE INDEX IF NOT EXISTS idx_categories_grade          ON categories (grade_id, is_active);

-- =============================================================
-- applicant_profiles
-- =============================================================
CREATE TABLE IF NOT EXISTS applicant_profiles
(
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID         NOT NULL UNIQUE,
    first_name       VARCHAR(100) NOT NULL,
    last_name        VARCHAR(100) NOT NULL,
    middle_name      VARCHAR(100),
    phone            VARCHAR(32),
    city             VARCHAR(120),
    country          VARCHAR(120),
    about            TEXT,
    experience_years SMALLINT,
    fsp_id           VARCHAR(64)  UNIQUE,
    fsp_linked_at    TIMESTAMPTZ,
    status           VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    version          INTEGER      NOT NULL DEFAULT 0,

    CONSTRAINT chk_applicant_profiles_status
    CHECK (status IN ('DRAFT','ACTIVE','HIDDEN')),
    CONSTRAINT chk_applicant_profiles_experience_years
    CHECK (experience_years IS NULL OR experience_years BETWEEN 0 AND 80),
    CONSTRAINT chk_applicant_profiles_fsp_linked
    CHECK ((fsp_id IS NULL AND fsp_linked_at IS NULL)
    OR (fsp_id IS NOT NULL AND fsp_linked_at IS NOT NULL))
);

CREATE INDEX IF NOT EXISTS idx_applicant_profiles_status     ON applicant_profiles (status);
CREATE INDEX IF NOT EXISTS idx_applicant_profiles_fsp_id     ON applicant_profiles (fsp_id) WHERE fsp_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_applicant_profiles_created_at ON applicant_profiles (created_at DESC);

-- =============================================================
-- applicant_skills
-- Grade verification per skill. Cooldown per (applicant_id, skill_id).
-- =============================================================
CREATE TABLE IF NOT EXISTS applicant_skills
(
    id                   UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    applicant_id         UUID        NOT NULL CONSTRAINT fk_applicant_skills_applicant REFERENCES applicant_profiles (id) ON DELETE CASCADE,
    skill_id             UUID        NOT NULL,
    self_assessed_level  SMALLINT,
    verified_grade_id    UUID,
    last_grade_change_at TIMESTAMPTZ,
    verified_at          TIMESTAMPTZ,
    is_primary           BOOLEAN     NOT NULL DEFAULT FALSE,
    years_experience     NUMERIC(3,1),
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    version              INTEGER     NOT NULL DEFAULT 0,

    CONSTRAINT uq_applicant_skills_applicant_skill
    UNIQUE (applicant_id, skill_id),
    CONSTRAINT chk_applicant_skills_self_assessed_level
    CHECK (self_assessed_level IS NULL OR self_assessed_level BETWEEN 1 AND 5),
    CONSTRAINT chk_applicant_skills_years_experience
    CHECK (years_experience IS NULL OR years_experience >= 0),
    CONSTRAINT chk_applicant_skills_verified_consistency
    CHECK ((verified_grade_id IS NULL AND verified_at IS NULL)
    OR (verified_grade_id IS NOT NULL AND verified_at IS NOT NULL))
);

CREATE INDEX IF NOT EXISTS idx_applicant_skills_applicant ON applicant_skills (applicant_id);
CREATE INDEX IF NOT EXISTS idx_applicant_skills_skill     ON applicant_skills (skill_id);
CREATE INDEX IF NOT EXISTS idx_applicant_skills_verified  ON applicant_skills (verified_grade_id)
    WHERE verified_grade_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_applicant_skills_one_primary
    ON applicant_skills (applicant_id)
    WHERE is_primary = TRUE;

-- =============================================================
-- applicant_experiences
-- =============================================================
CREATE TABLE IF NOT EXISTS applicant_experiences
(
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    applicant_id UUID         NOT NULL CONSTRAINT fk_applicant_experiences_applicant REFERENCES applicant_profiles (id) ON DELETE CASCADE,
    company      VARCHAR(200) NOT NULL,
    position     VARCHAR(200) NOT NULL,
    start_date   DATE         NOT NULL,
    end_date     DATE,
    is_current   BOOLEAN      NOT NULL DEFAULT FALSE,
    description  TEXT,
    sort_order   INTEGER      NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_applicant_experiences_period
    CHECK (end_date IS NULL OR end_date >= start_date),
    CONSTRAINT chk_applicant_experiences_current
    CHECK ((is_current = TRUE AND end_date IS NULL) OR (is_current = FALSE))
);

CREATE INDEX IF NOT EXISTS idx_applicant_experiences_applicant ON applicant_experiences (applicant_id, sort_order);
CREATE INDEX IF NOT EXISTS idx_applicant_experiences_current   ON applicant_experiences (applicant_id)
    WHERE is_current = TRUE;

-- =============================================================
-- applicant_educations
-- =============================================================
CREATE TABLE IF NOT EXISTS applicant_educations
(
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    applicant_id UUID         NOT NULL CONSTRAINT fk_applicant_educations_applicant REFERENCES applicant_profiles (id) ON DELETE CASCADE,
    institution  VARCHAR(200) NOT NULL,
    degree       VARCHAR(120),
    field        VARCHAR(200),
    start_year   SMALLINT,
    end_year     SMALLINT,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_applicant_educations_start_year
    CHECK (start_year IS NULL OR (start_year BETWEEN 1950 AND 2100)),
    CONSTRAINT chk_applicant_educations_end_year
    CHECK (end_year IS NULL OR (end_year BETWEEN 1950 AND 2100)),
    CONSTRAINT chk_applicant_educations_period
    CHECK (start_year IS NULL OR end_year IS NULL OR end_year >= start_year)
);

CREATE INDEX IF NOT EXISTS idx_applicant_educations_applicant ON applicant_educations (applicant_id);

-- =============================================================
-- applicant_resumes
-- Content lives in object storage. Here only metadata.
-- =============================================================
CREATE TABLE IF NOT EXISTS applicant_resumes
(
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    applicant_id UUID         NOT NULL CONSTRAINT fk_applicant_resumes_applicant REFERENCES applicant_profiles (id) ON DELETE CASCADE,
    file_url     VARCHAR(512) NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL DEFAULT 'application/pdf',
    size_bytes   BIGINT       NOT NULL,
    parsed_json  JSONB,
    is_primary   BOOLEAN      NOT NULL DEFAULT FALSE,
    uploaded_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_applicant_resumes_size
    CHECK (size_bytes > 0)
);

CREATE INDEX IF NOT EXISTS idx_applicant_resumes_applicant ON applicant_resumes (applicant_id, uploaded_at DESC);

CREATE UNIQUE INDEX IF NOT EXISTS uq_applicant_resumes_one_primary
    ON applicant_resumes (applicant_id)
    WHERE is_primary = TRUE;

-- =============================================================
-- applicant_privacy_settings
-- =============================================================
CREATE TABLE IF NOT EXISTS applicant_privacy_settings
(
    applicant_id               UUID        PRIMARY KEY CONSTRAINT fk_applicant_privacy_applicant REFERENCES applicant_profiles (id) ON DELETE CASCADE,
    visible_in_search          BOOLEAN     NOT NULL DEFAULT TRUE,
    allow_invitations          BOOLEAN     NOT NULL DEFAULT TRUE,
    show_contacts_after_accept BOOLEAN     NOT NULL DEFAULT TRUE,
    show_fsp_achievements      BOOLEAN     NOT NULL DEFAULT TRUE,
    updated_at                 TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- =============================================================
-- applicant_grade_change_history
-- =============================================================
CREATE TABLE IF NOT EXISTS applicant_grade_change_history
(
    id            UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    applicant_id  UUID        NOT NULL CONSTRAINT fk_applicant_grade_history_applicant REFERENCES applicant_profiles (id) ON DELETE CASCADE,
    skill_id      UUID        NOT NULL,
    from_grade_id UUID,
    to_grade_id   UUID        NOT NULL,
    reason        VARCHAR(20) NOT NULL,
    changed_at    TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_applicant_grade_history_reason
    CHECK (reason IN ('ASSESSMENT','ADMIN','SYSTEM')),
    CONSTRAINT chk_applicant_grade_history_diff
    CHECK (from_grade_id IS NULL OR from_grade_id <> to_grade_id)
);

CREATE INDEX IF NOT EXISTS idx_applicant_grade_history_lookup
    ON applicant_grade_change_history (applicant_id, skill_id, changed_at DESC);
CREATE INDEX IF NOT EXISTS idx_applicant_grade_history_applicant
    ON applicant_grade_change_history (applicant_id, changed_at DESC);

-- =============================================================
-- applicant_fsp_achievements_stub
-- Temporary. Ownership will move to fsp-integration.
-- =============================================================
CREATE TABLE IF NOT EXISTS applicant_fsp_achievements_stub
(
    id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    applicant_id UUID         NOT NULL CONSTRAINT fk_applicant_fsp_stub_applicant REFERENCES applicant_profiles (id) ON DELETE CASCADE,
    event_name   VARCHAR(255) NOT NULL,
    event_date   DATE,
    place        INTEGER,
    category     VARCHAR(120),
    verified     BOOLEAN      NOT NULL DEFAULT FALSE,
    source       VARCHAR(20)  NOT NULL DEFAULT 'STUB',
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_applicant_fsp_stub_source
    CHECK (source IN ('STUB','FSP_API')),
    CONSTRAINT chk_applicant_fsp_stub_place
    CHECK (place IS NULL OR place > 0)
);

CREATE INDEX IF NOT EXISTS idx_applicant_fsp_stub_applicant
    ON applicant_fsp_achievements_stub (applicant_id, event_date DESC);