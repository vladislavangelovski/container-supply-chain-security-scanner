CREATE EXTENSION IF NOT EXISTS "pgcrypto";

ALTER TABLE scan_report
ADD COLUMN sbom_json jsonb