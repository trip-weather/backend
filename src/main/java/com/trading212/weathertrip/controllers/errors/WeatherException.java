package com.trading212.weathertrip.controllers.errors;

public class WeatherException extends RuntimeException {
    public WeatherException(String message) {
        super(message);
    }
}
