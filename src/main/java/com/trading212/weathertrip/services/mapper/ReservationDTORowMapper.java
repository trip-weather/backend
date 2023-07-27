package com.trading212.weathertrip.services.mapper;

import com.trading212.weathertrip.domain.dto.ReservationDTO;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReservationDTORowMapper implements RowMapper<ReservationDTO> {
    @Override
    public ReservationDTO mapRow(ResultSet rs, int rowNum) throws SQLException {

        return ReservationDTO.builder()
                .checkInDate(rs.getDate("check_in_date").toString())
                .checkOutDate(rs.getDate("check_out_date").toString())
                .price(BigDecimal.valueOf(rs.getDouble("price")))
                .hotelName(rs.getString("name"))
                .city(rs.getString("city"))
                .photoMainUrl(rs.getString("photo_main_url"))
                .paymentStatus(rs.getString("payment_status"))
                .build();
    }
}
