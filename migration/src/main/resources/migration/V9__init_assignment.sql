CREATE TABLE IF NOT EXISTS vacancy_assignments
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vacancy_id     UUID         NOT NULL REFERENCES vacancies (id) ON DELETE CASCADE,
    title          VARCHAR(255) NOT NULL,
    description    TEXT         NOT NULL,
    duration_hours INTEGER      NOT NULL,
    is_active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_vacancy_assignments_vacancy UNIQUE (vacancy_id),
    CONSTRAINT chk_vacancy_assignments_duration CHECK (duration_hours > 0)
    );

CREATE INDEX IF NOT EXISTS idx_vacancy_assignments_vacancy ON vacancy_assignments (vacancy_id);
CREATE INDEX IF NOT EXISTS idx_vacancy_assignments_active  ON vacancy_assignments (is_active);

CREATE TABLE IF NOT EXISTS assignment_attempts
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    assignment_id UUID        NOT NULL REFERENCES vacancy_assignments (id) ON DELETE CASCADE,
    candidate_id UUID         NOT NULL,
    status       VARCHAR(32)  NOT NULL,
    started_at   TIMESTAMPTZ  NOT NULL,
    deadline_at  TIMESTAMPTZ  NOT NULL,
    submitted_at TIMESTAMPTZ,
    content_text TEXT,
    evaluated_at TIMESTAMPTZ,
    score        INTEGER,
    verdict      VARCHAR(32),
    feedback     TEXT,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_assignment_attempts_candidate UNIQUE (assignment_id, candidate_id),
    CONSTRAINT chk_assignment_attempts_status CHECK (status IN ('STARTED','SUBMITTED','EVALUATED','EXPIRED')),
    CONSTRAINT chk_assignment_attempts_verdict CHECK (verdict IS NULL OR verdict IN ('PASS','FAIL'))
    );

CREATE INDEX IF NOT EXISTS idx_assignment_attempts_assignment ON assignment_attempts (assignment_id);
CREATE INDEX IF NOT EXISTS idx_assignment_attempts_candidate  ON assignment_attempts (candidate_id);
CREATE INDEX IF NOT EXISTS idx_assignment_attempts_status     ON assignment_attempts (status);