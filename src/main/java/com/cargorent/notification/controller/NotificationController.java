package com.cargorent.notification.controller;

import com.cargorent.notification.common.ApiException;
import com.cargorent.notification.common.CurrentUser;
import com.cargorent.notification.dto.NotificationRequest;
import com.cargorent.notification.dto.NotificationResponse;
import com.cargorent.notification.service.NotificationService;
import com.cargorent.notification.service.ReminderJob;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final ReminderJob reminderJob;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Internal (booking-service): create a notification")
    public NotificationResponse create(CurrentUser user, @Valid @RequestBody NotificationRequest req) {
        if (!user.isInternal()) throw ApiException.forbidden("Internal endpoint");
        return notificationService.create(req);
    }

    @GetMapping("/my")
    public List<NotificationResponse> mine(CurrentUser user, @RequestParam(defaultValue = "50") int limit) {
        return notificationService.mine(user, limit);
    }

    @GetMapping("/my/unread-count")
    public Map<String, Long> unreadCount(CurrentUser user) {
        return Map.of("count", notificationService.unreadCount(user));
    }

    @PatchMapping("/{id:\\d+}/read")
    public NotificationResponse markRead(CurrentUser user, @PathVariable Long id) {
        return notificationService.markRead(user, id);
    }

    @PatchMapping("/my/read-all")
    public Map<String, Integer> markAllRead(CurrentUser user) {
        return Map.of("updated", notificationService.markAllRead(user));
    }

    @PostMapping("/reminders/run")
    @Operation(summary = "Admin: run the reminder job now instead of waiting for the schedule")
    public Map<String, Integer> runReminders(CurrentUser user) {
        user.requireAdmin();
        return Map.of("sent", reminderJob.run());
    }
}
