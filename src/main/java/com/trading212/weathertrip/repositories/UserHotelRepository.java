package com.trading212.weathertrip.repositories;


import com.trading212.weathertrip.domain.dto.hotel.FavouriteHotelDTO;
import com.trading212.weathertrip.services.mapper.UserFavouriteHotelRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.trading212.weathertrip.repositories.UserHotelRepository.Queries.DELETE;
import static com.trading212.weathertrip.repositories.UserHotelRepository.Queries.SAVE;

@Repository
public class UserHotelRepository {
    private final JdbcTemplate jdbcTemplate;
    private final UserFavouriteHotelRowMapper rowMapper = new UserFavouriteHotelRowMapper();

    public UserHotelRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean save(String userUuid, String hotelUuid) {
        int rowsAffected = jdbcTemplate.update(SAVE, userUuid, hotelUuid, LocalDateTime.now());
        return rowsAffected > 0;
    }

    public boolean delete(String userUuid, String hotelUuid) {
        int rowsAffected = jdbcTemplate.update(DELETE, userUuid, hotelUuid);
        return rowsAffected > 0;
    }

    public List<FavouriteHotelDTO> getUserFavouriteHotels(String uuid) {
        String sql = "select h.uuid,\n" +
                "       h.name,\n" +
                "       h.photo_main_url,\n" +
                "       h.favourite_count,\n" +
                "       h.city,\n" +
                "       h.external_id\n" +
                "from hotels as h\n" +
                "         join user_favourite_hotels ufh on h.uuid = ufh.hotel_uuid\n" +
                "where user_uuid = ?";

        return jdbcTemplate.query(sql, rowMapper, uuid);
    }

    public List<Integer> getUserFavouriteHotelsIds(String uuid) {
        String sql = "select h.external_id\n" +
                "from hotels as h\n" +
                "         join user_favourite_hotels ufh on h.uuid = ufh.hotel_uuid\n" +
                "where user_uuid = ?";

        return jdbcTemplate.queryForList(sql, Integer.class, uuid);
    }

    public boolean isHotelInUserFavorites(String userUuid, String hotelUuid) {
        String sql = "SELECT COUNT(*) AS count\n" +
                "FROM user_favourite_hotels\n" +
                "WHERE user_uuid = ? " +
                "  AND hotel_uuid = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{userUuid, hotelUuid}, Integer.class);

        return  count > 0;
    }

    static final class Queries {
        private Queries() {
        }

        public static final String SAVE = "INSERT INTO user_favourite_hotels(user_uuid, hotel_uuid, created_at)\n" +
                "VALUES (?, ?, ?)";

        public static final String DELETE = "DELETE from user_favourite_hotels\n" +
                "WHERE user_uuid = ? and hotel_uuid = ?";
    }

}
