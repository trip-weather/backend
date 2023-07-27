package com.trading212.weathertrip.services;

import com.trading212.weathertrip.controllers.errors.InvalidOrderException;
import com.trading212.weathertrip.domain.entities.Order;
import com.trading212.weathertrip.repositories.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void save(Order order) {
        orderRepository.save(order);
    }

    public void updateStripeOrder(Order order) {
        this.orderRepository.updateStripeOrder(order);
    }

    public Order getOrderByUuid(String orderUuid) {
        return this.orderRepository
                .findByUuid(orderUuid)
                .orElseThrow(() -> new InvalidOrderException("Order with uuid " + orderUuid + " not found"));
    }

    public void updateStatus(Order order) {
        this.orderRepository.updateStatus(order);
    }
}
