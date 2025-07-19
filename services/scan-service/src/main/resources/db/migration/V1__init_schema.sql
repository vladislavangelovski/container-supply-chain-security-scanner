CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE scan (
    id          UUID PRIMARY KEY,
    image_name  TEXT NOT NULL,
    status      TEXT NOT NULL,
    created_at  TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE scan_report (
    id         UUID PRIMARY KEY,
    scan_id    UUID NOT NULL REFERENCES scan(id) ON DELETE CASCADE,
    raw_json   jsonb NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now()
);
