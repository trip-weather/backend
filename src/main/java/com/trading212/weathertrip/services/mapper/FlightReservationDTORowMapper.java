package com.trading212.weathertrip.services.mapper;

import com.trading212.weathertrip.domain.dto.flight.FlightReservationDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FlightReservationDTORowMapper implements RowMapper<FlightReservationDTO> {
    @Override
    public FlightReservationDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return FlightReservationDTO.builder()
                .reservationDate(rs.getString("reservation_date"))
                .price(rs.getString("price"))
                .departingAt(rs.getString("departing_at"))
                .arrivingAt(rs.getString("arriving_at"))
                .from(rs.getString("from"))
                .to(rs.getString("to"))
                .currency(rs.getString("currency"))
                .paymentStatus(rs.getString("payment_status"))
                .build();
    }
}
