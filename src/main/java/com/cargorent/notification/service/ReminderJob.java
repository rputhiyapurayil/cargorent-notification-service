package com.cargorent.notification.service;

import com.cargorent.notification.client.BookingClient;
import com.cargorent.notification.dto.NotificationRequest;
import com.cargorent.notification.dto.UpcomingBookings;
import com.cargorent.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Reminder notification system: periodically asks booking-service for pickups and
 * returns due within the next day and sends each customer one reminder per booking.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderJob {

    public static final String PICKUP_REMINDER = "PICKUP_REMINDER";
    public static final String RETURN_REMINDER = "RETURN_REMINDER";
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final BookingClient bookingClient;
    private final NotificationRepository notifications;
    private final NotificationService notificationService;

    @Value("${cargorent.reminders.days-ahead:1}")
    private int daysAhead;

    @Scheduled(cron = "${cargorent.reminders.cron:0 */15 * * * *}")
    public void scheduled() {
        try {
            int sent = run();
            if (sent > 0) log.info("Sent {} reminders", sent);
        } catch (Exception e) {
            log.warn("Reminder job skipped, booking service unreachable: {}", e.getMessage());
        }
    }

    /** Returns how many reminders were sent. Safe to call repeatedly: duplicates are skipped. */
    public int run() {
        UpcomingBookings upcoming = bookingClient.upcoming(daysAhead);
        int sent = 0;
        for (UpcomingBookings.Item b : upcoming.pickups()) {
            if (notifications.existsByBookingIdAndType(b.id(), PICKUP_REMINDER)) continue;
            notificationService.create(new NotificationRequest(b.userId(), PICKUP_REMINDER,
                    "Pickup " + when(b.startDate()),
                    "Reminder: pick up your " + b.vehicleName() + " in " + b.pickupCity() + " on "
                            + DATE.format(b.startDate()) + ". Carry your driving licence.", b.id()));
            sent++;
        }
        for (UpcomingBookings.Item b : upcoming.returns()) {
            if (notifications.existsByBookingIdAndType(b.id(), RETURN_REMINDER)) continue;
            notificationService.create(new NotificationRequest(b.userId(), RETURN_REMINDER,
                    "Return due " + when(b.endDate()),
                    "Reminder: please return " + b.vehicleName() + " (" + b.registrationNumber() + ") by "
                            + DATE.format(b.endDate()) + ".", b.id()));
            sent++;
        }
        return sent;
    }

    private static String when(LocalDate date) {
        LocalDate today = LocalDate.now();
        if (date.equals(today)) return "today";
        if (date.equals(today.plusDays(1))) return "tomorrow";
        return "on " + DATE.format(date);
    }
}
