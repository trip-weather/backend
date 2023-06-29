package com.trading212.weathertrip.repositories;

import com.trading212.weathertrip.controllers.validation.RegisterUserValidation;
import com.trading212.weathertrip.domain.entities.User;
import com.trading212.weathertrip.services.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final UserRoleMapper rowMapper = new UserRoleMapper();

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    public void save(RegisterUserValidation user, String encryptedPassword) {
        String sql = "INSERT INTO users (uuid, email,username, password, activated) " +
                "VALUES (?, ?, ?, ?, ?)";

//        String selectRole = "SELECT id FROM roles WHERE name = ?";
//        Integer roleId = jdbcTemplate.queryForObject(selectRole, Integer.class, UserRole.USER.name());

//        String roleSql = "INSERT INTO user_roles (user_uuid, role_id) VALUES (?, ?)";

        String uuid = UUID.randomUUID().toString();
        jdbcTemplate.update(sql, uuid, user.getEmail(), user.getUsername(), encryptedPassword,
                false);

//        jdbcTemplate.update(roleSql, uuid, roleId);
    }

    public Optional<User> findByEmail(String email) {

        String query = "SELECT * FROM users as u WHERE u.email = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, rowMapper, email));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

//    public Optional<User> findByUsername(String username) {
//        String query = "SELECT u.uuid,\n" +
//                "       u.username,\n" +
//                "       u.email,\n" +
//                "       u.firstName,\n" +
//                "       u.lastName,\n" +
//                "       u.password,\n" +
//                "       u.activated,\n" +
//                "       ur.role_id,\n" +
//                "       r.name\n" +
//                "FROM users as u\n" +
//                "         join user_roles ur on u.uuid = ur.user_uuid\n" +
//                "         join roles r on r.id = ur.role_id\n" +
//                "WHERE u.username = ?;";
//
//        try {
//            return Optional.of(jdbcTemplate.queryForObject(query, rowMapper, username));
//        } catch (EmptyResultDataAccessException e) {
//            return Optional.empty();
//        }
//    }

    public Optional<User> findByUsername(String username){
        String query = "SELECT * FROM users as u WHERE u.username = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, rowMapper, username));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updatePassword(String uuid, String encryptedPassword) {
        String query = "UPDATE users SET password = ? WHERE uuid = ?";

        int rowsAffected = jdbcTemplate.update(query, encryptedPassword, uuid);

        if (rowsAffected > 0) {
            System.out.println("Password updated successfully.");
        } else {
            System.out.println("User not found or password unchanged.");
        }
    }
}
