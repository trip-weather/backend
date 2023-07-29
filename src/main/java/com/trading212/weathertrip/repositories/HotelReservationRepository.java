package com.trading212.weathertrip.repositories;

import com.trading212.weathertrip.domain.dto.HotelReservationDTO;
import com.trading212.weathertrip.domain.entities.HotelReservation;
import com.trading212.weathertrip.services.mapper.HotelReservationDTORowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class HotelReservationRepository {
    private final JdbcTemplate jdbcTemplate;

    private final HotelReservationDTORowMapper rowMapper = new HotelReservationDTORowMapper();

    public HotelReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(HotelReservation reservation) {
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

    public Optional<HotelReservationDTO> getReservationByOrderUuid(String uuid) {

        String sql = """
                select check_in_date, check_out_date, price, h.name, h.city, h.photo_main_url, o.payment_status from hotel_reservations as hr
                join orders o on hr.order_uuid = o.uuid
                join hotels h on h.uuid = hr.hotel_uuid                                                                   \s
                where order_uuid = ?""";

        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, rowMapper, uuid));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
