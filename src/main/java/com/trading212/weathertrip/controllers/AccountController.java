package com.trading212.weathertrip.controllers;

import com.trading212.weathertrip.controllers.errors.InvalidValidationKeyException;
import com.trading212.weathertrip.controllers.errors.UserNotFoundException;
import com.trading212.weathertrip.controllers.validation.*;
import com.trading212.weathertrip.controllers.validation.UpdateUserValidation;
import com.trading212.weathertrip.domain.dto.UserProfileDTO;
import com.trading212.weathertrip.domain.entities.User;
import com.trading212.weathertrip.services.MailService;
import com.trading212.weathertrip.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@Slf4j
public class AccountController {
    private final UserService userService;
    private final MailService mailService;

    public AccountController(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;

    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid RegisterUserValidation validation) {
        User registerUser = userService.registerUser(validation);
        log.info("Register new user ");

        mailService.sendActivationEmail(registerUser);
    }

    @GetMapping("/account/activate")
    public void activate(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateAccount(key);

        if (user.isEmpty()) {
            throw new InvalidValidationKeyException("Invalid validation key");
        }
    }

    @PostMapping("/account/change-password")
    public void changePassword(@RequestBody @Valid ChangePasswordValidation validation) {
        userService.changePassword(validation);
    }

    @PostMapping("/account/reset-password")
    public void requestPasswordReset(@RequestBody @Valid ResetPasswordEmailValidation validation) {
        Optional<User> user = userService.requestForResetPassword(validation.getEmail());

        if (user.isPresent()) {
            mailService.sendPasswordResetMail(user.get());
        } else {
            throw new UserNotFoundException("User with email : " + validation.getEmail() + " not found!");
        }
    }

    @PostMapping("/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody PasswordAndKeyValidation validation) {
        Optional<User> user = userService.completePasswordReset(validation.getNewPassword(), validation.getKey());

        if (user.isEmpty()) {
            throw new UserNotFoundException("User with this reset key not found");
        }
    }

    @GetMapping("/user/{uuid}/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable("uuid") String uuid) {
        return ResponseEntity.ok(userService.getUserProfileByUuid(uuid));
    }

    @PatchMapping("/user/{uuid}/update")
    public ResponseEntity update(@RequestBody @Valid UpdateUserValidation validation,
                                 @PathVariable("uuid") String uuid) {
        userService.update(validation, uuid);
        return ResponseEntity.ok().build();
    }
}
