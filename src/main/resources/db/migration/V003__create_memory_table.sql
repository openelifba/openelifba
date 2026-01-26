CREATE TABLE memory
(
    id                       UUID                NOT NULL,
    exercise_id              UUID                NOT NULL,
    category_id              UUID                NOT NULL,
    user_id                  UUID                NOT NULL,
    created_at               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    next_review_at           TIMESTAMP           NOT NULL,
    interval_second          BIGINT              NOT NULL,
    correct_count            INT       DEFAULT 0 NOT NULL,
    incorrect_count          INT       DEFAULT 0 NOT NULL,
    streak                   INT       DEFAULT 0 NOT NULL,
    response_time_millis     BIGINT              NOT NULL,
    last_reviewed_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_review_time_millis BIGINT    DEFAULT 0,

    PRIMARY KEY (id),
    UNIQUE (exercise_id, category_id, user_id)
);