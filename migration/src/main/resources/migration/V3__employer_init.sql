CREATE TABLE IF NOT EXISTS companies
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id                UUID         NOT NULL,
    name                    VARCHAR(255) NOT NULL,
    description             TEXT,
    industry                VARCHAR(255),
    website                 VARCHAR(512),
    contact_person_name     VARCHAR(255),
    contact_person_position VARCHAR(255),
    contact_email           VARCHAR(255),
    contact_phone           VARCHAR(64),
    logo_url                VARCHAR(512),
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT now()
    );

CREATE UNIQUE INDEX IF NOT EXISTS idx_companies_owner_id ON companies (owner_id);

CREATE TABLE IF NOT EXISTS hiring_needs
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id        UUID        NOT NULL REFERENCES companies (id) ON DELETE CASCADE,
    title             VARCHAR(255) NOT NULL,
    description       TEXT,
    specialization_id UUID        NOT NULL,
    grade_id          UUID        NOT NULL,
    salary_from       BIGINT,
    salary_to         BIGINT,
    format            VARCHAR(32) NOT NULL,
    location          VARCHAR(255),
    is_active         BOOLEAN     NOT NULL DEFAULT true,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_hiring_needs_company_id ON hiring_needs (company_id);
CREATE INDEX IF NOT EXISTS idx_hiring_needs_is_active ON hiring_needs (is_active);