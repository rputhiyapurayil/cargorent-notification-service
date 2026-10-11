package com.cargorent.notification.service;

import com.cargorent.notification.common.ApiException;
import com.cargorent.notification.common.CurrentUser;
import com.cargorent.notification.dto.NotificationRequest;
import com.cargorent.notification.dto.NotificationResponse;
import com.cargorent.notification.entity.Notification;
import com.cargorent.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notifications;

    @Transactional
    public NotificationResponse create(NotificationRequest req) {
        Notification n = new Notification();
        n.setUserId(req.userId());
        n.setType(req.type());
        n.setTitle(req.title());
        n.setMessage(req.message());
        n.setBookingId(req.bookingId());
        return NotificationResponse.from(notifications.save(n));
    }

    public List<NotificationResponse> mine(CurrentUser user, int limit) {
        return notifications.findByUserIdOrderBySentAtDescIdDesc(user.requireId(), PageRequest.of(0, Math.min(Math.max(limit, 1), 200)))
                .stream().map(NotificationResponse::from).toList();
    }

    public long unreadCount(CurrentUser user) {
        return notifications.countByUserIdAndReadFalse(user.requireId());
    }

    @Transactional
    public NotificationResponse markRead(CurrentUser user, Long id) {
        Notification n = notifications.findById(id)
                .filter(x -> x.getUserId().equals(user.requireId()))
                .orElseThrow(() -> ApiException.notFound("Notification not found"));
        n.setRead(true);
        return NotificationResponse.from(n);
    }

    @Transactional
    public int markAllRead(CurrentUser user) {
        return notifications.markAllRead(user.requireId());
    }
}
