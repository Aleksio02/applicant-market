CREATE TABLE IF NOT EXISTS interactions
(
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type                  VARCHAR(32)  NOT NULL,
    employer_id           UUID         NOT NULL,
    candidate_id          UUID         NOT NULL,
    vacancy_id            UUID,
    title                 VARCHAR(255),
    message               TEXT,
    salary_from           BIGINT       NOT NULL,
    salary_to             BIGINT       NOT NULL,
    status                VARCHAR(32)  NOT NULL,
    contacts_revealed_at  TIMESTAMPTZ,
    sent_at               TIMESTAMPTZ  NOT NULL DEFAULT now(),
    viewed_at             TIMESTAMPTZ,
    responded_at          TIMESTAMPTZ,
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_interactions_type CHECK (type IN ('INVITATION','APPLICATION')),
    CONSTRAINT chk_interactions_status CHECK (status IN ('SENT','VIEWED','ACCEPTED','REJECTED','WITHDRAWN')),
    CONSTRAINT chk_interactions_salary CHECK (salary_from <= salary_to),
    CONSTRAINT chk_interactions_application_vacancy
    CHECK (type <> 'APPLICATION' OR vacancy_id IS NOT NULL)
    );

CREATE INDEX IF NOT EXISTS idx_interactions_candidate   ON interactions (candidate_id, type, status);
CREATE INDEX IF NOT EXISTS idx_interactions_employer    ON interactions (employer_id, type, status);
CREATE INDEX IF NOT EXISTS idx_interactions_vacancy     ON interactions (vacancy_id) WHERE vacancy_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_interactions_created     ON interactions (created_at DESC);