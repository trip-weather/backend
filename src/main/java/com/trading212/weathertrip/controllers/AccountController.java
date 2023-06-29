package com.trading212.weathertrip.controllers;

import com.trading212.weathertrip.controllers.validation.ChangePasswordValidation;
import com.trading212.weathertrip.controllers.validation.RegisterUserValidation;
import com.trading212.weathertrip.services.MailService;
import com.trading212.weathertrip.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AccountController {
    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterUserValidation validation) {
        userService.registerUser(validation);
    }

    @PostMapping("/account/change-password")
    public void changePassword(@RequestBody @Valid ChangePasswordValidation validation){
        System.out.println("controller");
        userService.changePassword(validation);

    }
}
