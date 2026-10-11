package com.cargorent.notification.dto;

import java.time.LocalDate;
import java.util.List;

/** Shape of booking-service GET /bookings/upcoming. */
public record UpcomingBookings(List<Item> pickups, List<Item> returns) {

    public record Item(Long id, Long userId, String vehicleName, String registrationNumber, String pickupCity,
                       LocalDate startDate, LocalDate endDate) { }
}
