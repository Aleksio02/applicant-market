-- =============================================================
-- matching_queries
-- History of employer searches. Used for audit and repeated views.
-- =============================================================
CREATE TABLE IF NOT EXISTS matching_queries
(
    id             UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    employer_id    UUID         NOT NULL,
    vacancy_id     UUID,
    params_json    JSONB        NOT NULL,
    result_count   INTEGER      NOT NULL DEFAULT 0,
    page           INTEGER      NOT NULL DEFAULT 0,
    page_size      INTEGER      NOT NULL DEFAULT 20,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_matching_queries_result_count
        CHECK (result_count >= 0),
    CONSTRAINT chk_matching_queries_page
        CHECK (page >= 0),
    CONSTRAINT chk_matching_queries_page_size
        CHECK (page_size > 0 AND page_size <= 500)
);

CREATE INDEX IF NOT EXISTS idx_matching_queries_employer
    ON matching_queries (employer_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_matching_queries_vacancy
    ON matching_queries (vacancy_id) WHERE vacancy_id IS NOT NULL;

-- =============================================================
-- matching_explanations
-- Rank snapshot for each candidate in a query.
-- Immutable once written — explains historical decisions.
-- =============================================================
CREATE TABLE IF NOT EXISTS matching_explanations
(
    id             UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    query_id       UUID         NOT NULL
        CONSTRAINT fk_matching_explanations_query
            REFERENCES matching_queries (id) ON DELETE CASCADE,
    applicant_id   UUID         NOT NULL,
    rank_position  INTEGER      NOT NULL,
    rank_score     NUMERIC(6,5) NOT NULL,
    match_level    NUMERIC(6,5) NOT NULL,
    reasons_json   JSONB        NOT NULL,
    factors_json   JSONB        NOT NULL,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_matching_explanations_query_applicant
        UNIQUE (query_id, applicant_id),
    CONSTRAINT chk_matching_explanations_rank_position
        CHECK (rank_position >= 1),
    CONSTRAINT chk_matching_explanations_rank_score
        CHECK (rank_score BETWEEN 0 AND 10),
    CONSTRAINT chk_matching_explanations_match_level
        CHECK (match_level BETWEEN 0 AND 1)
);

CREATE INDEX IF NOT EXISTS idx_matching_explanations_query
    ON matching_explanations (query_id, rank_position);
CREATE INDEX IF NOT EXISTS idx_matching_explanations_applicant
    ON matching_explanations (applicant_id, created_at DESC);