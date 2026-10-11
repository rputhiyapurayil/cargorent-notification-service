CREATE TABLE notifications (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT        NOT NULL,
    type        VARCHAR(40)   NOT NULL,
    title       VARCHAR(150)  NOT NULL,
    message     VARCHAR(1000) NOT NULL,
    booking_id  BIGINT,
    is_read     BOOLEAN       NOT NULL DEFAULT FALSE,
    sent_at     DATETIME      NOT NULL,
    INDEX idx_notifications_user (user_id, sent_at),
    INDEX idx_notifications_booking_type (booking_id, type)
);
