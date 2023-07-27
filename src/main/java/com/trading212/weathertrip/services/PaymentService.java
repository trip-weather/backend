package com.trading212.weathertrip.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.trading212.weathertrip.domain.entities.Hotel;
import com.trading212.weathertrip.domain.entities.Order;
import com.trading212.weathertrip.domain.entities.Reservation;
import com.trading212.weathertrip.domain.entities.User;
import com.trading212.weathertrip.domain.enums.OrderStatus;
import com.trading212.weathertrip.services.hotel.HotelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.trading212.weathertrip.domain.constants.Constants.DATE_FORMATTER;
import static com.trading212.weathertrip.domain.constants.Constants.PROFILE_DOMAIN;

@Service
@Slf4j
public class PaymentService {

    private static final String SUCCESS_PAYMENT_URL = PROFILE_DOMAIN + "/payment-outcome?order_uuid=%s";
    private static final String CANCEL_PAYMENT_URL = PROFILE_DOMAIN + "/payment-outcome?order_uuid=%s";

    private final HotelService hotelService;
    private final OrderService orderService;
    private final AuthService authService;
    private final ReservationService reservationService;

    public PaymentService(HotelService hotelService, OrderService orderService, AuthService authService, ReservationService reservationService) {
        this.hotelService = hotelService;
        this.orderService = orderService;
        this.authService = authService;
        this.reservationService = reservationService;
    }

    public Optional<Session> createPaymentSession(Integer externalId, BigDecimal amount, String checkIn, String checkOut) {
        Stripe.apiKey = "sk_test_51NXn6mJAyJlvQQVyByoBNiBSMDOhqgRmHlLYnCBO5dYIrjSJSrLpfyUFzstiSSJIErCftbz7yDOdanuN5uPJSu65007LHHmPEk";

        User user = authService.getAuthenticatedUser();
        Hotel hotel = hotelService.findByExternalId(externalId);

        Order order = createInitializedOrder(user, amount);
        orderService.save(order);


        Map<String, String> sessionMetadata = createSessionMetadata(order, hotel.getUuid(), checkIn, checkOut);

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .putAllMetadata(sessionMetadata)
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(String.format(SUCCESS_PAYMENT_URL, order.getUuid()))
                        .setCancelUrl(String.format(CANCEL_PAYMENT_URL, order.getUuid()))
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency(order.getCurrency())
                                                        .setUnitAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName(order.getCurrency())
                                                                        .setDescription("Поръчка")
                                                                        .build())
                                                        .build()
                                        ).build())

                        .build();

        try {
            Session session = Session.create(params);

            order.setStripeOrder(session.getId());
            orderService.updateStripeOrder(order);

            return Optional.of(session);
        } catch (StripeException e) {
            log.info("{}", e.getMessage());
            return Optional.empty();
        }
    }

    public void checkoutSessionCompleted(Session session) {
        Map<String, String> metadata = session.getMetadata();

        String orderUuid = metadata.get("order_uuid");
        String userUuid = metadata.get("user_uuid");
        String hotelUuid = metadata.get("hotel_uuid");
        String checkInString = metadata.get("check_in");
        String checkOutString = metadata.get("check_out");
        String price = metadata.get("amount");

        LocalDate checkIn = LocalDate.parse(checkInString, DATE_FORMATTER);
        LocalDate checkOut = LocalDate.parse(checkOutString, DATE_FORMATTER);

        Order order = orderService.getOrderByUuid(orderUuid);
        order.setStatus(OrderStatus.SUCCEEDED);
        orderService.updateStatus(order);

        Reservation reservation = Reservation.builder()
                .userUuid(userUuid)
                .hotelUuid(hotelUuid)
                .orderUuid(orderUuid)
                .reservationDate(LocalDateTime.now())
                .checkIn(checkIn)
                .checkOut(checkOut)
                .price(new BigDecimal(price))
                .build();

        reservationService.save(reservation);
    }

    private Map<String, String> createSessionMetadata(Order order, String hotelUuid, String checkIn, String checkOut) {
        Map<String, String> sessionMetadata = new HashMap<>();
        sessionMetadata.put("user_uuid", order.getUserUuid());
        sessionMetadata.put("hotel_uuid", hotelUuid);
        sessionMetadata.put("check_in", checkIn);
        sessionMetadata.put("check_out", checkOut);
        sessionMetadata.put("order_uuid", String.valueOf(order.getUuid()));
        sessionMetadata.put("amount", order.getPaymentAmount().toString());
        return sessionMetadata;
    }

    private Order createInitializedOrder(User user, BigDecimal amount) {
        return Order.builder()
                .userUuid(user.getUuid())
                .status(OrderStatus.INITIALIZED)
                .ordered(LocalDateTime.now())
                .paymentAmount(amount)
                .currency("BGN")
                .build();
    }

    public void checkoutSessionExpired(Session session) {
        String orderUuid = session.getMetadata().get("order_uuid");

        Order order = orderService.getOrderByUuid(orderUuid);
        order.setStatus(OrderStatus.CANCELLED);
        orderService.updateStatus(order);
    }
}
