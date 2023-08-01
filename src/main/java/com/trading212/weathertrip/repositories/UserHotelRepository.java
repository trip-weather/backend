package com.trading212.weathertrip.repositories;


import com.trading212.weathertrip.domain.dto.hotel.FavouriteHotelDTO;
import com.trading212.weathertrip.services.mapper.UserFavouriteHotelRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.trading212.weathertrip.repositories.UserHotelRepository.Queries.*;

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
        return jdbcTemplate.query(GET_USER_FAVOURITE_HOTELS, rowMapper, uuid);
    }

    public List<Integer> getUserFavouriteHotelsIds(String uuid) {
        return jdbcTemplate.queryForList(GET_USER_FAVOURITE_HOTEL_IDS, Integer.class, uuid);
    }

    public boolean isHotelInUserFavorites(String userUuid, String hotelUuid) {
        Integer count = jdbcTemplate.queryForObject(IS_HOTEL_IN_USER_FAVOURITES, new Object[]{userUuid, hotelUuid}, Integer.class);
        return count > 0;
    }

    static final class Queries {
        private Queries() {
        }

        public static final String GET_USER_FAVOURITE_HOTELS = """
                select h.uuid,
                       h.name,
                       h.photo_main_url,
                       h.favourite_count,
                       h.city,
                       h.external_id
                from hotels as h
                         join user_favourite_hotels ufh on h.uuid = ufh.hotel_uuid
                where user_uuid = ?""";

        public static final String IS_HOTEL_IN_USER_FAVOURITES = """
                SELECT COUNT(*) AS count
                FROM user_favourite_hotels
                WHERE user_uuid = ?   AND hotel_uuid = ?""";
        public static final String SAVE = "INSERT INTO user_favourite_hotels(user_uuid, hotel_uuid, created_at)\n" +
                "VALUES (?, ?, ?)";

        public static final String DELETE = "DELETE from user_favourite_hotels\n" +
                "WHERE user_uuid = ? and hotel_uuid = ?";

        public static final String GET_USER_FAVOURITE_HOTEL_IDS = """
                select h.external_id
                from hotels as h
                         join user_favourite_hotels ufh on h.uuid = ufh.hotel_uuid
                where user_uuid = ?""";
    }

}
