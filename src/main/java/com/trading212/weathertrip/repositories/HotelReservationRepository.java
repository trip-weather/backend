package com.trading212.weathertrip.repositories;

import com.trading212.weathertrip.domain.dto.hotel.HotelReservationDTO;
import com.trading212.weathertrip.domain.dto.UserReservedHotelsDTO;
import com.trading212.weathertrip.domain.entities.HotelReservation;
import com.trading212.weathertrip.services.mapper.HotelReservationDTORowMapper;
import com.trading212.weathertrip.services.mapper.UserReservedHotelsRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.trading212.weathertrip.repositories.HotelReservationRepository.Queries.*;

@Repository
public class HotelReservationRepository {
    private final JdbcTemplate jdbcTemplate;

    private final HotelReservationDTORowMapper rowMapper = new HotelReservationDTORowMapper();
    private final UserReservedHotelsRowMapper reservedHotelsRowMapper = new UserReservedHotelsRowMapper();

    public HotelReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(HotelReservation reservation) {
        jdbcTemplate.update(INSERT_INTO_HOTEL_RESERVATIONS,
                reservation.getUserUuid(),
                reservation.getHotelUuid(),
                reservation.getOrderUuid(),
                reservation.getReservationDate(),
                reservation.getCheckIn(),
                reservation.getCheckOut(),
                reservation.getPrice());
    }

    public Optional<HotelReservationDTO> getReservationByOrderUuid(String uuid) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(GET_RESERVATION_BY_ORDER_UUID, rowMapper, uuid));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<UserReservedHotelsDTO> getUserReservedHotelsByStatus(String uuid, String status) {
        String sql = "";
        if (status.equals("past")) {
            sql = GET_USER_RESERVED_PAST_HOTELS;
        } else if (status.equals("future")) {
            sql = GET_USER_RESERVED_FUTURE_AND_NOW_HOTELS;
        }
        try {
            return jdbcTemplate.query(sql, reservedHotelsRowMapper, uuid);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    static final class Queries {

        public static final String GET_USER_RESERVED_PAST_HOTELS = """
                select reservation_date, check_in_date, check_out_date, price, photo_main_url, city, external_id, currency
                from hotel_reservations as hs
                         join hotels h on h.uuid = hs.hotel_uuid
                join orders o on o.uuid = hs.order_uuid
                where hs.user_uuid = ? and o.order_type = 'hotel'
                 and check_out_date < now()""";
        public static final String GET_USER_RESERVED_FUTURE_AND_NOW_HOTELS = """
                select reservation_date, check_in_date, check_out_date, price, photo_main_url, city, external_id, currency
                from hotel_reservations as hs
                         join hotels h on h.uuid = hs.hotel_uuid
                join orders o on o.uuid = hs.order_uuid
                where hs.user_uuid = ? and o.order_type = 'hotel'
                 and check_out_date >= now()""";

        public static final String GET_RESERVATION_BY_ORDER_UUID = """
                select check_in_date, check_out_date, price, h.name, h.city, h.photo_main_url, o.payment_status from hotel_reservations as hr
                join orders o on hr.order_uuid = o.uuid
                join hotels h on h.uuid = hr.hotel_uuid                                                                   \s
                where order_uuid = ?""";

        public static final String INSERT_INTO_HOTEL_RESERVATIONS = "insert into hotel_reservations(user_uuid, hotel_uuid, order_uuid, reservation_date, check_in_date, check_out_date, price) VALUES (?,  ?, ?, ?, ?, ?, ?)";
    }
}
