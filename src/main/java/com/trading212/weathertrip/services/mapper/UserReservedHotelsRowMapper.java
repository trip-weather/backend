package com.trading212.weathertrip.services.mapper;

import com.trading212.weathertrip.domain.dto.UserReservedHotelsDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserReservedHotelsRowMapper implements RowMapper<UserReservedHotelsDTO> {
    @Override
    public UserReservedHotelsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserReservedHotelsDTO.builder()
                .reservationDate(rs.getTimestamp("reservation_date").toString())
                .checkInDate(rs.getDate("check_in_date").toString())
                .checkOutDate(rs.getDate("check_out_date").toString())
                .price(rs.getBigDecimal("price"))
                .photoMainUrl(rs.getString("photo_main_url"))
                .city(rs.getString("city"))
                .externalId(rs.getInt("external_id"))
                .currency(rs.getString("currency"))
                .build();
    }
}
