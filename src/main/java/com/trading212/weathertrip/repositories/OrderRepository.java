package com.trading212.weathertrip.repositories;

import com.trading212.weathertrip.domain.entities.Order;
import com.trading212.weathertrip.services.mapper.OrderMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderRepository {
    private final JdbcTemplate jdbcTemplate;
    private final OrderMapper orderMapper = new OrderMapper();

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Order save(Order order) {
        String sql = "insert into orders (uuid, user_uuid, ordered, payment_status, amount, currency) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String uuid = UUID.randomUUID().toString();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, uuid);
            ps.setString(2, order.getUserUuid());
            ps.setTimestamp(3, Timestamp.valueOf(order.getOrdered()));
            ps.setString(4, order.getStatus().name());
            ps.setBigDecimal(5, order.getPaymentAmount());
            ps.setString(6, order.getCurrency());

            return ps;

        }, keyHolder);

        keyHolder.getKey();

        order.setUuid(uuid);
        return order;
    }

    public void updateStripeOrder(Order order) {
        String sql = "update orders \n" +
                "set stripe_order = ?\n" +
                "where uuid = ?";

        jdbcTemplate.update(sql, order.getStripeOrder(), order.getUuid());
    }

    public Optional<Order> findByUuid(String orderUuid) {

        String sql = "select uuid, user_uuid, ordered, payment_status, stripe_order, currency, amount\n" +
                "from orders\n" +
                "where uuid = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, orderMapper, orderUuid));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updateStatus(Order order) {
        String sql = "update orders \n" +
                "set payment_status = ?\n" +
                "where uuid = ?";

        jdbcTemplate.update(sql, order.getStatus().name(), order.getUuid());
    }
}
