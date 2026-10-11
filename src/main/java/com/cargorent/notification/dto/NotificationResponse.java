package com.cargorent.notification.dto;

import com.cargorent.notification.entity.Notification;

import java.time.LocalDateTime;

public record NotificationResponse(Long id, Long userId, String type, String title, String message,
                                   Long bookingId, boolean read, LocalDateTime sentAt) {

    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(n.getId(), n.getUserId(), n.getType(), n.getTitle(), n.getMessage(),
                n.getBookingId(), n.isRead(), n.getSentAt());
    }
}
