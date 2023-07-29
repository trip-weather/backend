package com.trading212.weathertrip.domain.entities;

import com.trading212.weathertrip.domain.enums.OrderStatus;
import com.trading212.weathertrip.domain.enums.OrderType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Order {
    private String uuid;

    private String stripeOrder;

    private LocalDateTime ordered;

    private String userUuid;

    private BigDecimal paymentAmount;

    private String currency;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private OrderType type;
}
