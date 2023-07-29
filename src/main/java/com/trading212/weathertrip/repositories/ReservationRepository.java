package com.trading212.weathertrip.repositories;

import com.trading212.weathertrip.domain.dto.ReservationDTO;
import com.trading212.weathertrip.domain.entities.Reservation;
import com.trading212.weathertrip.services.mapper.ReservationDTORowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ReservationRepository {
    private final JdbcTemplate jdbcTemplate;

    private final ReservationDTORowMapper rowMapper = new ReservationDTORowMapper();

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Reservation reservation) {
        String sql = "insert into hotel_reservations(user_uuid, hotel_uuid, order_uuid, reservation_date, check_in_date, check_out_date, price) VALUES (?,  ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                reservation.getUserUuid(),
                reservation.getHotelUuid(),
                reservation.getOrderUuid(),
                reservation.getReservationDate(),
                reservation.getCheckIn(),
                reservation.getCheckOut(),
                reservation.getPrice());
    }

    public Optional<ReservationDTO> getReservationByOrderUuid(String uuid) {

        String sql = "select check_in_date, check_out_date, price, h.name, h.city, h.photo_main_url, o.payment_status from hotel_reservations\n" +
                "join orders o on hotel_reservations.order_uuid = o.uuid\n" +
                "join hotels h on h.uuid = hotel_reservations.hotel_uuid                                                                    \n" +
                "where order_uuid = ?";

        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, rowMapper, uuid));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
