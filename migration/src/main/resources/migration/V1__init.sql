CREATE TABLE IF NOT EXISTS users
(
    id                UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    username          VARCHAR(255) NOT NULL UNIQUE,
    email             VARCHAR(255) NOT NULL UNIQUE,
    password          VARCHAR(255) NOT NULL,
    role              VARCHAR(32)  NOT NULL,
    status            VARCHAR(32)  NOT NULL,
    email_verified_at TIMESTAMPTZ
    );

CREATE TABLE IF NOT EXISTS consents
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    type        VARCHAR(64) NOT NULL,
    version     INT         NOT NULL,
    accepted_at TIMESTAMPTZ NOT NULL,
    revoked_at  TIMESTAMPTZ
    );

CREATE INDEX IF NOT EXISTS idx_consents_user_id ON consents (user_id);