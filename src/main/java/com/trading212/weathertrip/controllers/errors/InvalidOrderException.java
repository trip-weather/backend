package com.trading212.weathertrip.controllers.errors;

public class InvalidOrderException extends RuntimeException{
    public InvalidOrderException(String message){
        super(message);
    }
}
