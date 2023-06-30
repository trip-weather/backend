package com.trading212.weathertrip.repositories;

import com.trading212.weathertrip.controllers.validation.RegisterUserValidation;
import com.trading212.weathertrip.domain.dto.UserDTO;
import com.trading212.weathertrip.domain.entities.User;
import com.trading212.weathertrip.services.mapper.DetailedUserRowMapper;
import com.trading212.weathertrip.services.mapper.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate txTemplate;
    private final UserRowMapper rowMapper = new UserRowMapper();
    private final DetailedUserRowMapper detailedRowMapper = new DetailedUserRowMapper();

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate, TransactionTemplate txTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.txTemplate = txTemplate;
    }


    public User save(User user) {
        return txTemplate.execute(status -> {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            String uuid = UUID.randomUUID().toString();
            jdbcTemplate.update(conn -> {

                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (uuid, username, email, firstName, lastName, password, activated, activation_key, created_date ) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);


                ps.setString(1, uuid);
                ps.setString(2, user.getUsername());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getFirstName());
                ps.setString(5, user.getLastName());
                ps.setString(6, user.getPassword());
                ps.setBoolean(7, user.isActivated());
                ps.setString(8, user.getActivationKey());
                ps.setTimestamp(9, Timestamp.from(Instant.from(user.getCreatedDate())));

                return ps;
            }, keyHolder);

            jdbcTemplate.update("INSERT INTO user_authority(authority_name, user_uuid) VALUES (?, ?)", "ROLE_USER", uuid);
            return user;
        });
    }

    public Optional<User> findByEmail(String email) {
        String query = "SELECT uuid, username, email, password, firstName, lastName, activated, reset_key, reset_date" +
                " FROM users as u WHERE u.email = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, rowMapper, email));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    public Optional<User> findByUsername(String username) {
        String query = """
                SELECT uuid, username, email, password, firstName, lastName, activated
                from users 
                where username = ?
                """;

        try {
            return Optional.of(jdbcTemplate.queryForObject(query, rowMapper, username));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByUsernameDetailedInfo(String username) {
        String query = """
                SELECT uuid, username, email, password, firstName, lastName, activated, reset_key, reset_date
                from users 
                where username = ?
                """;

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, detailedRowMapper, username));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updatePassword(String uuid, String encryptedPassword) {
        String query = "UPDATE users SET password = ? WHERE uuid = ?";
        jdbcTemplate.update(query, encryptedPassword, uuid);
    }

    public void updateResetKeyAndDate(User user) {
        String query = "UPDATE users \n" +
                "SET reset_date = ?,  reset_key = ?\n" +
                "WHERE email = ?";

        jdbcTemplate.update(query, user.getResetDate(), user.getResetKey(), user.getEmail());
    }

    public Optional<User> findByResetKey(String key) {
        String query = "SELECT uuid, username, email, password, firstName, lastName, activated, reset_key, reset_date " +
                "from users " +
                "where reset_key = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, detailedRowMapper, key));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void resetPassword(User user) {
        String query = "UPDATE users\n" +
                "SET password = ?, reset_key = ?, reset_date = ?\n" +
                "WHERE email = ?";

        jdbcTemplate.update(query, user.getPassword(), user.getResetKey(), user.getResetDate(), user.getEmail());
    }

    public Optional<User> findByActivationKey(String key) {
        String query = "SELECT uuid, username, email, password, firstName, lastName, activated, reset_key, reset_date " +
                "from users " +
                "where activation_key = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, detailedRowMapper, key));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void saveUpdateActivation(User user) {
        String query = "UPDATE users\n" +
                "SET activated = ?, activation_key = ? \n" +
                "WHERE uuid = ?";

        jdbcTemplate.update(query, user.isActivated(), user.getActivationKey(), user.getUuid());
    }
}
