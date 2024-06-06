DROP TABLE IF EXISTS order_overdue__event_internal;
DROP TABLE IF EXISTS order_overdue__event_incoming;
DROP TABLE IF EXISTS order_overdue__failure;

CREATE TABLE order_overdue__event_internal
(
    id           UUID PRIMARY KEY,
    aggregate_id UUID         NOT NULL,
    occurred_at  TIMESTAMP    NOT NULL,
    event_type   VARCHAR(255) NOT NULL,
    payload      VARCHAR      NOT NULL
);

CREATE TABLE order_overdue__event_internal_archive
(
    id           UUID PRIMARY KEY,
    aggregate_id UUID         NOT NULL,
    occurred_at  TIMESTAMP    NOT NULL,
    event_type   VARCHAR(255) NOT NULL,
    payload      VARCHAR      NOT NULL,
    archived_at  TIMESTAMP    NOT NULL
);

CREATE TABLE order_overdue__event_incoming
(
    id      VARCHAR(255) PRIMARY KEY,
    payload VARCHAR NOT NULL
);

CREATE TABLE order_overdue__failure
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