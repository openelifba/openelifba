CREATE TABLE exercise
(
    id          UUID PRIMARY KEY,
    value       TEXT NOT NULL,
    audio_url   TEXT NOT NULL,
    rank        INT  NOT NULL,
    category_id UUID NOT NULL REFERENCES category (id)
);