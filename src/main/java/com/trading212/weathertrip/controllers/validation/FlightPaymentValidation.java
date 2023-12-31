package com.trading212.weathertrip.controllers.validation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class FlightPaymentValidation {

    @NotNull
    private BigDecimal amount;

    private Flight outgoingFlight;

    private Flight incomingFlight;
    @NotNull
    private String from;

    @NotNull
    private String to;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Flight {
        @NotNull
        private String departingAt;

        @NotNull
        private String arrivingAt;
    }


}
