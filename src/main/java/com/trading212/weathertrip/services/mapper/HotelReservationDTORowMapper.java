package com.trading212.weathertrip.services.mapper;

import com.trading212.weathertrip.domain.dto.HotelReservationDTO;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HotelReservationDTORowMapper implements RowMapper<HotelReservationDTO> {
    @Override
    public HotelReservationDTO mapRow(ResultSet rs, int rowNum) throws SQLException {

        return HotelReservationDTO.builder()
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
