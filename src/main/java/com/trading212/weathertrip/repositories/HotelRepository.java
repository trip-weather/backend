package com.trading212.weathertrip.repositories;

import com.trading212.weathertrip.domain.dto.hotel.HotelResultDTO;
import com.trading212.weathertrip.domain.dto.hotel.WrapperHotelDTO;
import com.trading212.weathertrip.domain.entities.Hotel;
import com.trading212.weathertrip.services.mapper.HotelRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.trading212.weathertrip.repositories.HotelRepository.Queries.*;

@Repository
public class HotelRepository {
    private final JdbcTemplate jdbcTemplate;
    private final HotelRowMapper hotelMapper = new HotelRowMapper();

    public HotelRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(List<WrapperHotelDTO> hotels) {
        for (WrapperHotelDTO hotel : hotels) {
            List<HotelResultDTO> hotelResults = hotel.getResults();

            jdbcTemplate.batchUpdate(SAVE, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    HotelResultDTO hotelResult = hotelResults.get(i);

                    ps.setString(1, UUID.randomUUID().toString());
                    ps.setInt(2, hotelResult.getId());
                    ps.setString(3, hotelResult.getName());
                    ps.setString(4, "Booking API");
                    ps.setString(5, hotelResult.getPhotoMainUrl());
                    ps.setString(6, hotelResult.getWishlistName());
                }

                @Override
                public int getBatchSize() {
                    return hotelResults.size();
                }
            });
        }
    }

    public Optional<Hotel> findByExternalId(int id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_EXTERNAL_ID, hotelMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Hotel> findByUuid(String uuid) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_UUID, hotelMapper, uuid));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean updateFavouriteCount(Hotel hotel) {
        int rowsAffected = jdbcTemplate.update(UPDATE_FAVOURITE_COUNT, hotel.getFavouriteCount(), hotel.getUuid());

        return rowsAffected > 0;
    }

    static final class Queries {
        private Queries() {
        }

        public static final String SAVE = "insert ignore into hotels(uuid, external_id, name, provider, photo_main_url, city)" +
                " VALUES (?, ?, ?, ?, ?, ?)";
        public static final String UPDATE_FAVOURITE_COUNT = """
                UPDATE hotels\s
                SET favourite_count = ?
                WHERE uuid = ?""";

        public static final String FIND_BY_UUID = """
                select uuid, external_id, name, provider, favourite_count\s
                from hotels
                where uuid = ?""";

        public static final String FIND_BY_EXTERNAL_ID = """
                select uuid, external_id, name, provider,favourite_count \s
                from hotels
                where external_id = ?""";
    }
}
