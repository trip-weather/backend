package com.trading212.weathertrip.services.mapper;

import com.trading212.weathertrip.domain.entities.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DetailedUserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Timestamp resetDateTimestamp = resultSet.getTimestamp("reset_date");
        LocalDateTime resetDate = resetDateTimestamp != null ? resetDateTimestamp.toLocalDateTime() : null;

        return User.builder()
                .uuid(resultSet.getString("uuid"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .username(resultSet.getString("username"))
                .firstName(resultSet.getString("firstName"))
                .lastName(resultSet.getString("lastName"))
                .activated(resultSet.getBoolean("activated"))
                .resetKey(resultSet.getString("reset_key"))
                .resetDate(resetDate)
//                .authorities(Set.of(UserRole.USER))
                .build();
    }
}
