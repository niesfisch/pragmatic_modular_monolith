DROP TABLE IF EXISTS failure;

CREATE TABLE failure
(
    id           UUID PRIMARY KEY,
    type         VARCHAR(255) NOT NULL,
    use_case     VARCHAR(255) NOT NULL,
    payload      VARCHAR      NOT NULL,
    cause        VARCHAR      NOT NULL,
    error_code   VARCHAR(255) NOT NULL,
    created      TIMESTAMP    NOT NULL,
    trigger_type VARCHAR(255) NOT NULL,
    trigger      VARCHAR      NOT NULL,
    stack_trace  VARCHAR      NOT NULL
);
