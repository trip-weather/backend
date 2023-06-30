package com.trading212.weathertrip.services.mapper;

import com.trading212.weathertrip.domain.entities.Authority;
import com.trading212.weathertrip.domain.entities.User;
import com.trading212.weathertrip.domain.enums.UserRole;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .uuid(resultSet.getString("uuid"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .username(resultSet.getString("username"))
                .firstName(resultSet.getString("firstName"))
                .lastName(resultSet.getString("lastName"))
                .authorities(Set.of(new Authority(1L, "ROLE_USER")))
                .build();
    }

    private Set<UserRole> extractRoles(ResultSet resultSet) throws SQLException {
        Set<UserRole> userRoles = new HashSet<>();

        while (resultSet.next()) {
            String roleName = resultSet.getString("name");
            UserRole userRole = UserRole.valueOf(roleName.toUpperCase());
            userRoles.add(userRole);
        }
        return userRoles;
    }
}
