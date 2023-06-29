package com.trading212.weathertrip.repositories;

import com.trading212.weathertrip.domain.entities.Authority;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRoleRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRoleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Authority> findRoleUserUuid(String uuid) {
        String query = "select id, authority_name from user_authority where user_uuid = ?";

        try {
          return   Optional.of(jdbcTemplate.queryForObject(query, (rs, rowNum) ->
                    new Authority(rs.getLong("id"), rs
                            .getString("authority_name")), uuid));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
