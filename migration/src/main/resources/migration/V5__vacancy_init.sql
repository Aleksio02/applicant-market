CREATE TABLE IF NOT EXISTS vacancies
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id        UUID         NOT NULL,
    title             VARCHAR(255) NOT NULL,
    description       TEXT         NOT NULL,
    specialization_id UUID         NOT NULL,
    grade_id          UUID         NOT NULL,
    salary_from       BIGINT       NOT NULL,
    salary_to         BIGINT       NOT NULL,
    format            VARCHAR(32)  NOT NULL,
    location          VARCHAR(255),
    status            VARCHAR(32)  NOT NULL DEFAULT 'DRAFT',
    published_at      TIMESTAMPTZ,
    closed_at         TIMESTAMPTZ,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_vacancies_salary CHECK (salary_from <= salary_to),
    CONSTRAINT chk_vacancies_status CHECK (status IN ('DRAFT','PUBLISHED','CLOSED'))
    );

CREATE INDEX IF NOT EXISTS idx_vacancies_company_id ON vacancies (company_id, status);
CREATE INDEX IF NOT EXISTS idx_vacancies_status     ON vacancies (status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_vacancies_spec_grade ON vacancies (specialization_id, grade_id, status);

CREATE TABLE IF NOT EXISTS vacancy_requirements
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vacancy_id   UUID        NOT NULL REFERENCES vacancies (id) ON DELETE CASCADE,
    skill_id     UUID        NOT NULL,
    level        SMALLINT    NOT NULL,
    is_mandatory BOOLEAN     NOT NULL DEFAULT false,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_vacancy_requirements_vacancy_skill UNIQUE (vacancy_id, skill_id),
    CONSTRAINT chk_vacancy_requirements_level CHECK (level BETWEEN 1 AND 5)
    );

CREATE INDEX IF NOT EXISTS idx_vacancy_requirements_vacancy ON vacancy_requirements (vacancy_id);
CREATE INDEX IF NOT EXISTS idx_vacancy_requirements_skill   ON vacancy_requirements (skill_id);