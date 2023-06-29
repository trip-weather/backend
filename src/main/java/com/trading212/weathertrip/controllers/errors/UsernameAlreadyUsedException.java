package com.trading212.weathertrip.controllers.errors;

public class UsernameAlreadyUsedException extends RuntimeException{
    public UsernameAlreadyUsedException(String message) {
        super(message);
    }
}
