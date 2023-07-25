package com.trading212.weathertrip.controllers.errors;

public class FlightException extends RuntimeException {
    public FlightException(String message) {
        super(message);
    }
}
