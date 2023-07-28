package com.trading212.weathertrip.controllers.errors.exceptionHandler;

import com.trading212.weathertrip.controllers.errors.InvalidFlightException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FlightExceptionHandler {
    @ExceptionHandler(InvalidFlightException.class)
    public ResponseEntity<String> handleInvalidFlight(InvalidFlightException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
