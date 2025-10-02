DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS notification_attempt;
DROP TABLE IF EXISTS notification;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL
);

CREATE TABLE notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    idempotency_key VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    retry_count INT NOT NULL DEFAULT 0,
    max_retry INT NOT NULL DEFAULT 2,
    phone_number VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    contents VARCHAR(1000) NOT NULL,
    reserved_at TIMESTAMP NOT NULL,
    retry_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uk_idempotency_key UNIQUE (idempotency_key)
);

CREATE INDEX idx_status_retry_at ON notification(status, retry_at);
CREATE INDEX idx_user_id_reserved_at ON notification(user_id, reserved_at);
CREATE INDEX idx_user_id_created_at ON notification(user_id, created_at);

CREATE TABLE notification_attempt (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    notification_id BIGINT NOT NULL,
    attempt_no INT NOT NULL,
    result VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notification_id FOREIGN KEY (notification_id) REFERENCES notification(id),
    CONSTRAINT uk_notification_id UNIQUE (notification_id)
);