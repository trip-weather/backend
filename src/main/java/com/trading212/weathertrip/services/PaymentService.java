package com.trading212.weathertrip.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.trading212.weathertrip.controllers.validation.FlightPaymentValidation;
import com.trading212.weathertrip.domain.entities.*;
import com.trading212.weathertrip.domain.enums.OrderStatus;
import com.trading212.weathertrip.domain.enums.OrderType;
import com.trading212.weathertrip.services.hotel.HotelReservationService;
import com.trading212.weathertrip.services.hotel.HotelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.trading212.weathertrip.domain.constants.APIConstants.STRIPE_API_KEY;
import static com.trading212.weathertrip.domain.constants.Constants.*;

@Service
@Slf4j
public class PaymentService {

    private final HotelService hotelService;
    private final OrderService orderService;
    private final AuthService authService;
    private final HotelReservationService hotelReservationService;
    private final FlightService flightService;
    private final FlightReservationService flightReservationService;


    public PaymentService(HotelService hotelService, OrderService orderService, AuthService authService, HotelReservationService reservationService, FlightService flightService, FlightReservationService flightReservationService) {
        this.hotelService = hotelService;
        this.orderService = orderService;
        this.authService = authService;
        this.hotelReservationService = reservationService;
        this.flightService = flightService;
        this.flightReservationService = flightReservationService;
    }

    public Optional<Session> createPaymentSession(Integer externalId, BigDecimal amount, String checkIn, String checkOut) {
        Stripe.apiKey = STRIPE_API_KEY;

        User user = authService.getAuthenticatedUser();
        Hotel hotel = hotelService.findByExternalId(externalId);

        Order order = createInitializedOrder(user, amount, OrderType.HOTEL);
        orderService.save(order);

        Map<String, String> sessionMetadata = createHotelSessionMetadata(order, hotel.getUuid(), checkIn, checkOut);

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .putAllMetadata(sessionMetadata)
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(String.format(SUCCESS_HOTEL_PAYMENT_URL, order.getUuid()))
                        .setCancelUrl(String.format(CANCEL_HOTEL_PAYMENT_URL, order.getUuid()))
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency(order.getCurrency())
                                                        .setUnitAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName(hotel.getName())
                                                                        .setDescription("Резервация на хотел")
                                                                        .build())
                                                        .build()
                                        ).build())

                        .build();

        return createSession(order, params);
    }

    public Optional<Session> createFlightPaymentSession(FlightPaymentValidation validation) {

        Stripe.apiKey = STRIPE_API_KEY;
        User user = authService.getAuthenticatedUser();
        Order order = createInitializedOrder(user, validation.getAmount(), OrderType.FLIGHT);
        orderService.save(order);

        Map<String, String> sessionMetadata = createFlightSessionMetadata(order, validation);

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .putAllMetadata(sessionMetadata)
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(String.format(SUCCESS_FLIGHT_PAYMENT_URL, order.getUuid()))
                        .setCancelUrl(String.format(CANCEL_FLIGHT_PAYMENT_URL, order.getUuid()))
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency(order.getCurrency())
                                                        .setUnitAmount(validation.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("Самолетни билети")
                                                                        .setDescription("Поръчка на самолетни билети")
                                                                        .build())
                                                        .build()
                                        ).build())

                        .build();

        return createSession(order, params);
    }


    private Optional<Session> createSession(Order order, SessionCreateParams params) {
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

    private Map<String, String> createFlightSessionMetadata(Order order,
                                                            FlightPaymentValidation validation) {
        Map<String, String> sessionMetadata = new HashMap<>();
        sessionMetadata.put("order_type", order.getType().name());
        sessionMetadata.put("order_uuid", order.getUuid());
        sessionMetadata.put("user_uuid", order.getUserUuid());
        sessionMetadata.put("outgoingFlightDepartAt", validation.getOutgoingFlight().getDepartingAt());
        sessionMetadata.put("outgoingFlightArriveAt", validation.getOutgoingFlight().getArrivingAt());
        sessionMetadata.put("incomingFlightDepartAt", validation.getIncomingFlight().getDepartingAt());
        sessionMetadata.put("incomingFlightArriveAt", validation.getIncomingFlight().getArrivingAt());
        sessionMetadata.put("from", validation.getFrom());
        sessionMetadata.put("to", validation.getTo());
        sessionMetadata.put("amount", order.getPaymentAmount().toString());
        return sessionMetadata;
    }

    public void checkoutHotelSessionCompleted(Session session) {
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

        HotelReservation reservation = HotelReservation.builder()
                .userUuid(userUuid)
                .hotelUuid(hotelUuid)
                .orderUuid(orderUuid)
                .reservationDate(LocalDateTime.now())
                .checkIn(checkIn)
                .checkOut(checkOut)
                .price(new BigDecimal(price))
                .build();

        hotelReservationService.save(reservation);
    }


    private Map<String, String> createHotelSessionMetadata(Order order,
                                                           String hotelUuid,
                                                           String checkIn,
                                                           String checkOut) {

        Map<String, String> sessionMetadata = new HashMap<>();
        sessionMetadata.put("order_type", order.getType().name());
        sessionMetadata.put("user_uuid", order.getUserUuid());
        sessionMetadata.put("hotel_uuid", hotelUuid);
        sessionMetadata.put("check_in", checkIn);
        sessionMetadata.put("check_out", checkOut);
        sessionMetadata.put("order_uuid", String.valueOf(order.getUuid()));
        sessionMetadata.put("amount", order.getPaymentAmount().toString());
        return sessionMetadata;
    }

    private Order createInitializedOrder(User user, BigDecimal amount, OrderType type) {
        return Order.builder()
                .userUuid(user.getUuid())
                .status(OrderStatus.INITIALIZED)
                .type(type)
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

    public void checkoutFlightSessionCompleted(Session session) {
        Map<String, String> metadata = session.getMetadata();

        String orderUuid = metadata.get("order_uuid");
        String userUuid = metadata.get("user_uuid");
        String outgoingFlightDepartAtString = metadata.get("outgoingFlightDepartAt");
        String outgoingFlightArriveAtString = metadata.get("outgoingFlightArriveAt");
        String incomingFlightDepartAtString = metadata.get("incomingFlightDepartAt");
        String incomingFlightArriveAtString = metadata.get("incomingFlightArriveAt");
        String from = metadata.get("from");
        String to = metadata.get("to");
        String price = metadata.get("amount");


        LocalDate outgoingFlightDepartAt = LocalDate.parse(outgoingFlightDepartAtString, DATE_TIME_FORMATTER);
        LocalDate outgoingFlightArriveAt = LocalDate.parse(outgoingFlightArriveAtString, DATE_TIME_FORMATTER);

        LocalDate incomingFlightDepartAt = LocalDate.parse(incomingFlightDepartAtString, DATE_TIME_FORMATTER);
        LocalDate incomingFlightArriveAt = LocalDate.parse(incomingFlightArriveAtString, DATE_TIME_FORMATTER);

        Order order = orderService.getOrderByUuid(orderUuid);
        order.setStatus(OrderStatus.SUCCEEDED);
        orderService.updateStatus(order);

        Flight firstFlight = Flight.builder()
                .from(from)
                .to(to)
                .provider("Duffel API")
                .departingAt(outgoingFlightDepartAt)
                .arrivingAt(outgoingFlightArriveAt)
                .build();


        Flight secondFlight = Flight.builder()
                .from(to)
                .to(from)
                .provider("Duffel API")
                .departingAt(incomingFlightDepartAt)
                .arrivingAt(incomingFlightArriveAt)
                .build();

        // TODO SaveAll method
        flightService.save(firstFlight);
        flightService.save(secondFlight);

        FlightReservation reservation = FlightReservation.builder()
                .userUuid(userUuid)
                .flightUuid(firstFlight.getUuid())
                .orderUuid(orderUuid)
                .reservationDate(LocalDate.now())
                .price(new BigDecimal(price))
                .build();

        FlightReservation reservation2 = FlightReservation.builder()
                .userUuid(userUuid)
                .flightUuid(secondFlight.getUuid())
                .orderUuid(orderUuid)
                .reservationDate(LocalDate.now())
                .price(new BigDecimal(price))
                .build();

        // TODO SaveAll method
        flightReservationService.save(reservation2);
        flightReservationService.save(reservation);
    }

    public void checkOrderType(Session session) {
        String orderType = session.getMetadata().get("order_type");

        if (orderType.equals("HOTEL")) {
            checkoutHotelSessionCompleted(session);
        } else if (orderType.equals("FLIGHT")) {
            checkoutFlightSessionCompleted(session);
        }
    }
}
