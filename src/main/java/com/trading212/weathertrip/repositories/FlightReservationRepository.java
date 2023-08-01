package com.trading212.weathertrip.repositories;

import com.trading212.weathertrip.domain.dto.flight.FlightReservationDTO;
import com.trading212.weathertrip.domain.dto.UserReservedFlightsDTO;
import com.trading212.weathertrip.domain.entities.FlightReservation;
import com.trading212.weathertrip.services.mapper.FlightReservationDTORowMapper;
import com.trading212.weathertrip.services.mapper.UserReservedFlightsRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static com.trading212.weathertrip.domain.constants.Constants.STATUS_FUTURE;
import static com.trading212.weathertrip.domain.constants.Constants.STATUS_PAST;
import static com.trading212.weathertrip.repositories.FlightReservationRepository.Queries.*;

@Repository
public class FlightReservationRepository {
    private final JdbcTemplate jdbcTemplate;
    private final FlightReservationDTORowMapper flightReservationRowMapper = new FlightReservationDTORowMapper();
    private final UserReservedFlightsRowMapper userReservedFlightsRowMapper = new UserReservedFlightsRowMapper();

    public FlightReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(FlightReservation reservation) {
        jdbcTemplate.update(INSERT_INTO_FLIGHT_RESERVATIONS,
                reservation.getUserUuid(),
                reservation.getFlightUuid(),
                reservation.getOrderUuid(),
                reservation.getReservationDate(),
                reservation.getPrice());
    }

    public List<FlightReservationDTO> getReservationByOrderUuid(String uuid) {
        try {
            return jdbcTemplate.query(GET_RESERVATION_BY_ORDER_UUID, flightReservationRowMapper, uuid);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    public List<UserReservedFlightsDTO> getUserReservedFlightsByStatus(String uuid, String status) {
        String sql = "";
        if (STATUS_PAST.equals(status)) {
            sql = GET_USER_RESERVED_PAST_FLIGHT;
        } else if (STATUS_FUTURE.equals(status)) {
            sql = GET_USER_RESERVED_FUTURE_AND_NOW_FLIGHTS;
        }
        try {
            return jdbcTemplate.query(sql, userReservedFlightsRowMapper, uuid);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    static final class Queries {

        public static final String GET_USER_RESERVED_PAST_FLIGHT = """
                select reservation_date, price, `from`, `to`, departing_at, arriving_at
                from flight_reservations as fr
                         join flights f on f.uuid = fr.flight_uuid
                join orders o on fr.order_uuid = o.uuid
                where fr.user_uuid = ? and order_type = 'flight' and arriving_at < now()""";

        public static final String GET_USER_RESERVED_FUTURE_AND_NOW_FLIGHTS = """
                select reservation_date, price, `from`, `to`, departing_at, arriving_at
                from flight_reservations as fr
                         join flights f on f.uuid = fr.flight_uuid
                join orders o on fr.order_uuid = o.uuid
                where fr.user_uuid = ? and order_type = 'flight' and arriving_at >= now()""";

        public static final String GET_RESERVATION_BY_ORDER_UUID = """
                select reservation_date, price, f.departing_at, f.arriving_at, f.from, f.`to`, o.currency, o.payment_status
                from flight_reservations
                         join orders o on flight_reservations.order_uuid = o.uuid
                         join flights f on f.uuid = flight_reservations.flight_uuid
                where order_uuid = ?""";

        public static final String INSERT_INTO_FLIGHT_RESERVATIONS = "insert into flight_reservations(user_uuid, flight_uuid, order_uuid, reservation_date, price) " +
                " values (?, ?, ?, ?, ?)";
    }
}
