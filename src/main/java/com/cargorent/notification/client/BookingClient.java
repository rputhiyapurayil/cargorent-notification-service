package com.cargorent.notification.client;

import com.cargorent.notification.dto.UpcomingBookings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BookingClient {

    private final RestClient rest;

    public BookingClient(RestClient.Builder builder, @Value("${services.booking.url}") String baseUrl) {
        this.rest = builder.baseUrl(baseUrl).build();
    }

    public UpcomingBookings upcoming(int days) {
        return rest.get().uri(u -> u.path("/bookings/upcoming").queryParam("days", days).build())
                .retrieve().body(UpcomingBookings.class);
    }
}
