package com.trading212.weathertrip.services.mapper;

import com.trading212.weathertrip.domain.entities.Hotel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HotelRowMapper implements RowMapper<Hotel> {
    @Override
    public Hotel mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Hotel.builder()
                .uuid(rs.getString("uuid"))
                .externalId(rs.getInt("external_id"))
                .name(rs.getString("name"))
                .provider(rs.getString("provider"))
                .favouriteCount(rs.getInt("favourite_count"))
                .build();
    }
}
