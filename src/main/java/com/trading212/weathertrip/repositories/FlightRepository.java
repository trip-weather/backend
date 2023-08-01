package com.trading212.weathertrip.repositories;

import com.trading212.weathertrip.domain.entities.Flight;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;

import static com.trading212.weathertrip.repositories.FlightRepository.Queries.INSERT_INTO_FLIGHTS;

@Repository
public class FlightRepository {
    private final JdbcTemplate jdbcTemplate;

    public FlightRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Flight save(Flight flight) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String uuid = UUID.randomUUID().toString();

        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(
                    INSERT_INTO_FLIGHTS,
                    Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, uuid);
            ps.setString(2, flight.getFrom());
            ps.setString(3, flight.getTo());
            ps.setString(4, flight.getProvider());
            ps.setDate(5, Date.valueOf(flight.getDepartingAt()));
            ps.setDate(6, Date.valueOf(flight.getArrivingAt()));

            return ps;

        }, keyHolder);

        flight.setUuid(uuid);
        return flight;
    }

    public static class Queries {
        public static final String INSERT_INTO_FLIGHTS = "insert into flights(`uuid`, `from`, `to`, provider, departing_at, arriving_at) " +
                " VALUES (?, ?, ?, ?, ?, ?)";
    }
}
