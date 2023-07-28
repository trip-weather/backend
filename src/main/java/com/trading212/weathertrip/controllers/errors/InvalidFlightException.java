package com.trading212.weathertrip.controllers.errors;

public class InvalidFlightException extends RuntimeException {
    public InvalidFlightException(String message) {
        super(message);
    }
}
