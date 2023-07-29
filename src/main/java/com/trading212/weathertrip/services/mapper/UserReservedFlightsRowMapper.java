package com.trading212.weathertrip.services.mapper;

import com.trading212.weathertrip.domain.dto.UserReservedFlightsDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserReservedFlightsRowMapper implements RowMapper<UserReservedFlightsDTO> {
    @Override
    public UserReservedFlightsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserReservedFlightsDTO.builder()
                .reservationDate(rs.getString("reservation_date"))
                .price(rs.getString("price"))
                .from(rs.getString("from"))
                .to(rs.getString("to"))
                .departingAt(rs.getString("departing_at"))
                .arrivingAt(rs.getString("arriving_at"))
                .build();
    }
}
