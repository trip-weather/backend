package com.trading212.weathertrip.controllers;

import com.google.gson.JsonSyntaxException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.trading212.weathertrip.services.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

import static com.trading212.weathertrip.domain.constants.APIConstants.STRIPE_ENDPOINT_SECRET;

@Slf4j
@RestController
@RequestMapping("/api")
public class PaymentController {
    private final PaymentService paymentService;


    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/hotel/create-verification-session")
    public ResponseEntity<String> createHotelPaymentSession(@RequestParam(name = "id") Integer externalId,
                                                            @RequestParam(name = "amount") BigDecimal amount,
                                                            @RequestParam(name = "check_in") String checkIn,
                                                            @RequestParam(name = "check_out") String checkOut) {

        Optional<Session> session = paymentService.createPaymentSession(externalId, amount, checkIn, checkOut);

        if (session.isPresent()) {
            String sessionUrl = session.get().getUrl();
            return ResponseEntity.ok(sessionUrl);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to create payment session. Please try again later.");
    }

    @PostMapping("/flight/create-verification-session")
    public ResponseEntity<String> createFlightPaymentSession(@RequestParam(name = "amount") BigDecimal amount,
                                                             @RequestParam(name = "departing_at") String departAt,
                                                             @RequestParam(name = "arriving_at") String arriveAt,
                                                             @RequestParam(name = "from") String from,
                                                             @RequestParam(name = "to") String to) {

        Optional<Session> session = paymentService.createFlightPaymentSession(amount, departAt, arriveAt, from, to);

        if (session.isPresent()) {
            String sessionUrl = session.get().getUrl();
            return ResponseEntity.ok(sessionUrl);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to create payment session. Please try again later.");
    }

    @PostMapping("/payment")
    public void hotelPayment(@RequestBody String payload,
                             @RequestHeader("Stripe-Signature") String signHeader) {

        try {
            Event event = Webhook.constructEvent(payload, signHeader, STRIPE_ENDPOINT_SECRET);

            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = dataObjectDeserializer.getObject().orElseThrow();

            switch (event.getType()) {
                case "checkout.session.completed": {
                    Session session = (Session) stripeObject;
                    paymentService.checkoutHotelSessionCompleted(session);
                    break;
                }
                case "checkout.session.expired": {
                    Session session = (Session) stripeObject;
                    paymentService.checkoutSessionExpired(session);
                    break;
                }
                default:
                    System.out.println("Unhandled event type: " + event.getType());
            }

        } catch (JsonSyntaxException e) { // Invalid payload
            log.info("Invalid payload");
        } catch (SignatureVerificationException e) { // Invalid signature
            log.info("Invalid signature");
        }
    }

    @PostMapping("/payment-flight")
    public void flightPayment(@RequestBody String payload,
                              @RequestHeader("Stripe-Signature") String signHeader) {

        String endpointSecret = "whsec_63cf9dbcdff16b197fbc412a8dd45531eba740dd449aac4edadf417c88b56255";

        try {
            Event event = Webhook.constructEvent(payload, signHeader, endpointSecret);
            System.out.println(event);
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = dataObjectDeserializer.getObject().orElseThrow();

            switch (event.getType()) {
                case "checkout.session.completed": {
                    Session session = (Session) stripeObject;
                    paymentService.checkoutFlightSessionCompleted(session);
                    break;
                }
                case "checkout.session.expired": {
                    Session session = (Session) stripeObject;
                    paymentService.checkoutSessionExpired(session);
                    break;
                }
                default:
                    System.out.println("Unhandled event type: " + event.getType());
            }

        } catch (JsonSyntaxException e) { // Invalid payload
            log.info("Invalid payload");
        } catch (SignatureVerificationException e) { // Invalid signature
            log.info("Invalid signature");
        }
    }
}
