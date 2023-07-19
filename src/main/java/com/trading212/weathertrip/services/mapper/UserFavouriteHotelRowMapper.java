package com.trading212.weathertrip.services.mapper;

import com.trading212.weathertrip.domain.dto.hotel.FavouriteHotelDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserFavouriteHotelRowMapper implements RowMapper<FavouriteHotelDTO> {
    @Override
    public FavouriteHotelDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        FavouriteHotelDTO hotel = new FavouriteHotelDTO();
        hotel.setUuid(rs.getString("uuid"));
        hotel.setName(rs.getString("name"));
        hotel.setPhotoMainUrl(rs.getString("photo_main_url"));
        hotel.setFavouriteCount(rs.getInt("favourite_count"));
        hotel.setCity(rs.getString("city"));
        hotel.setExternalId(rs.getInt("external_id"));

        return hotel;
    }
}
