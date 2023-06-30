package com.trading212.weathertrip.controllers.validation;

public class InvalidValidationKeyException extends RuntimeException{
    public InvalidValidationKeyException(String message){
        super(message);
    }
}
