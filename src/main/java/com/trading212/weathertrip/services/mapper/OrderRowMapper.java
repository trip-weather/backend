package com.trading212.weathertrip.services.mapper;

import com.trading212.weathertrip.domain.entities.Order;
import com.trading212.weathertrip.domain.enums.OrderStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class OrderRowMapper implements RowMapper<Order> {
    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        LocalDateTime ordered = rs.getTimestamp("ordered").toLocalDateTime();

        return Order.builder()
                .uuid(rs.getString("uuid"))
                .userUuid(rs.getString("user_uuid"))
                .ordered(ordered)
                .status(OrderStatus.valueOf(rs.getString("payment_status")))
                .stripeOrder(rs.getString("stripe_order"))
                .paymentAmount(rs.getBigDecimal("amount"))
                .currency(rs.getString("currency"))
                .build();
    }
}
