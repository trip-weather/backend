package com.trading212.weathertrip.repositories;

import com.trading212.weathertrip.domain.entities.Order;
import com.trading212.weathertrip.services.mapper.OrderMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import static com.trading212.weathertrip.repositories.OrderRepository.Queries.*;

@Repository
public class OrderRepository {
    private final JdbcTemplate jdbcTemplate;
    private final OrderMapper orderMapper = new OrderMapper();

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Order save(Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String uuid = UUID.randomUUID().toString();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(
                    INSERT_INTO_ORDERS,
                    Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, uuid);
            ps.setString(2, order.getUserUuid());
            ps.setTimestamp(3, Timestamp.valueOf(order.getOrdered()));
            ps.setString(4, order.getStatus().name());
            ps.setBigDecimal(5, order.getPaymentAmount());
            ps.setString(6, order.getCurrency());
            ps.setString(7, order.getType().name());

            return ps;

        }, keyHolder);

        keyHolder.getKey();

        order.setUuid(uuid);
        return order;
    }

    public void updateStripeOrder(Order order) {
        jdbcTemplate.update(UPDATE_STRIPE_ORDER, order.getStripeOrder(), order.getUuid());
    }

    public Optional<Order> findByUuid(String orderUuid) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_ORDER_BY_UUID, orderMapper, orderUuid));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updateStatus(Order order) {
        jdbcTemplate.update(UPDATE_ORDER_STATUS, order.getStatus().name(), order.getUuid());
    }

    static final class Queries {
        public static final String UPDATE_ORDER_STATUS = """
                update orders\s
                set payment_status = ?
                where uuid = ?""";

        public static final String FIND_ORDER_BY_UUID = """
                select uuid, user_uuid, ordered, payment_status, stripe_order, currency, amount
                from orders
                where uuid = ?""";

        public static final String UPDATE_STRIPE_ORDER = """
                update orders\s
                set stripe_order = ?
                where uuid = ?""";

        public static final String INSERT_INTO_ORDERS = "insert into orders (uuid, user_uuid, ordered, payment_status, amount, currency, order_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }
}
