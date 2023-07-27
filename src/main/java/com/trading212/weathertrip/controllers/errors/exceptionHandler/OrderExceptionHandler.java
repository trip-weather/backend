package com.trading212.weathertrip.controllers.errors.exceptionHandler;

import com.trading212.weathertrip.controllers.errors.InvalidOrderException;
import com.trading212.weathertrip.controllers.errors.WeatherException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExceptionHandler {
    @ExceptionHandler(WeatherException.class)
    public ResponseEntity<String> handleInvalidOrder(InvalidOrderException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
