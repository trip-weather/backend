package com.trading212.weathertrip.controllers.errors.exceptionHandler;

import com.trading212.weathertrip.controllers.errors.EmailAlreadyUsedException;
import com.trading212.weathertrip.controllers.errors.UsernameAlreadyUsedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InvalidUserExceptionHandler {

    @ExceptionHandler({UsernameAlreadyUsedException.class, EmailAlreadyUsedException.class})
    public ResponseEntity<String> handleInvalidUserException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
