package com.trading212.weathertrip.repositories;

import com.trading212.weathertrip.controllers.validation.UpdateUserValidation;
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
import java.util.Optional;
import java.util.UUID;

import static com.trading212.weathertrip.repositories.UserRepository.Queries.*;

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
                        INSERT_INTO_USERS,
                        Statement.RETURN_GENERATED_KEYS);

                ps.setString(1, uuid);
                ps.setString(2, user.getUsername());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getFirstName());
                ps.setString(5, user.getLastName());
                ps.setString(6, user.getPassword());
                ps.setBoolean(7, user.isActivated());
                ps.setString(8, user.getActivationKey());
                ps.setTimestamp(9, Timestamp.valueOf(user.getCreatedDate()));

                return ps;
            }, keyHolder);

            jdbcTemplate.update(INSERT_INTO_USER_AUTHORITY, "ROLE_USER", uuid);
            return user;
        });
    }

    public Optional<User> findByEmail(String email) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_EMAIL, rowMapper, email));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    public Optional<User> findByUsername(String username) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(FIND_BY_USERNAME, rowMapper, username));
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
        jdbcTemplate.update(UPDATE_PASSWORD, encryptedPassword, uuid);
    }

    public void updateResetKeyAndDate(User user) {
        jdbcTemplate.update(UPDATE_RESET_KEY_AND_DATE, user.getResetDate(), user.getResetKey(), user.getEmail());
    }

    public Optional<User> findByResetKey(String key) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_RESET_KEY, detailedRowMapper, key));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void resetPassword(User user) {
        jdbcTemplate.update(RESET_USER_PASSWORD, user.getPassword(), user.getResetKey(), user.getResetDate(), user.getEmail());
    }

    public Optional<User> findByActivationKey(String key) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(Queries.FIND_BY_ACTIVATION_KEY, detailedRowMapper, key));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void saveUpdateActivation(User user) {
        jdbcTemplate.update(UPDATE_USER_ACTIVATION, user.isActivated(), user.getActivationKey(), user.getUuid());
    }

    public Optional<User> findByUsernameOrEmail(String username) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(FIND_BY_USERNAME_OR_EMAIL, rowMapper, username, username));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updateUser(UpdateUserValidation validation, String uuid) {
        String sql = "update users\n" +
                "set firstName = ?, lastName = ?\n" +
                "where uuid = ?";

        jdbcTemplate.update(sql, validation.getFirstName(), validation.getLastName(), uuid);
    }


    static final class Queries {

        private Queries() {

        }

        public static final String INSERT_INTO_USER_AUTHORITY = "INSERT INTO user_authority(authority_name, user_uuid) VALUES (?, ?)";
        public static final String INSERT_INTO_USERS = "INSERT INTO users (uuid, username, email, firstName, lastName, password, activated, activation_key, created_date ) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        public static final String FIND_BY_EMAIL = "SELECT uuid, username, email, password, firstName, lastName, activated, reset_key, reset_date" +
                " FROM users as u WHERE u.email = ?";
        public static final String FIND_BY_USERNAME = """
                SELECT uuid, username, email, password, firstName, lastName, activated
                from users
                where username = ?
                """;
        public static final String UPDATE_PASSWORD = "UPDATE users SET password = ? WHERE uuid = ?";
        public static final String UPDATE_RESET_KEY_AND_DATE = """
                UPDATE users\s
                SET reset_date = ?,  reset_key = ?
                WHERE email = ?""";
        public static final String FIND_BY_RESET_KEY = "SELECT uuid, username, email, password, firstName, lastName, activated, reset_key, reset_date " +
                "from users " +
                "where reset_key = ?";
        public static final String RESET_USER_PASSWORD = """
                UPDATE users
                SET password = ?, reset_key = ?, reset_date = ?
                WHERE email = ?""";
        public static final String FIND_BY_ACTIVATION_KEY = "SELECT uuid, username, email, password," +
                " firstName, lastName, activated, reset_key, reset_date " +
                "from users " +
                "where activation_key = ?";
        public static final String UPDATE_USER_ACTIVATION = "UPDATE users\n" +
                "SET activated = ?, activation_key = ? \n" +
                "WHERE uuid = ?";
        public static final String FIND_BY_USERNAME_OR_EMAIL = """
                SELECT uuid, username, email, password, firstName, lastName, activated
                from users
                where username = ? or email = ?;
                """;
    }
}
