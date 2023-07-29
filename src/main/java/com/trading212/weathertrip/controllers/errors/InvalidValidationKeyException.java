package com.trading212.weathertrip.controllers.errors;

public class InvalidValidationKeyException extends RuntimeException{
    public InvalidValidationKeyException(String message){
        super(message);
    }
}
