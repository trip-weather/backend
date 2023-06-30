package com.trading212.weathertrip.controllers.validation;

import com.trading212.weathertrip.domain.constants.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserValidation {
    @NotBlank
    @Size(min = Constants.USERNAME_MIN_SIZE, max = Constants.USERNAME_MAX_SIZE)
    private String username;

    @Email
    @Size(min = 5, max = 100)
    private String email;

    @Size(min = Constants.PASSWORD_MIN_LENGTH, max = Constants.PASSWORD_MAX_LENGTH)
    private String password;

    private String firstName;
    private String lastName;
}
