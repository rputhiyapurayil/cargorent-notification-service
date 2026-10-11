package com.cargorent.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificationRequest(
        @NotNull Long userId,
        @NotBlank @Size(max = 40) String type,
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 1000) String message,
        Long bookingId) {
}
