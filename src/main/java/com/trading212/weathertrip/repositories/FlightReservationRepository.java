package com.trading212.weathertrip.repositories;

import com.trading212.weathertrip.domain.entities.FlightReservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FlightReservationRepository {
    private final JdbcTemplate jdbcTemplate;

    public FlightReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(FlightReservation reservation) {
        String sql = "insert into flight_reservations(user_uuid, flight_uuid, order_uuid, reservation_date, price)\n" +
                "values (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                reservation.getUserUuid(),
                reservation.getFlightUuid(),
                reservation.getOrderUuid(),
                reservation.getReservationDate(),
                reservation.getPrice());
    }
}
