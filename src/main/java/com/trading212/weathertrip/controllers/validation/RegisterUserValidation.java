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

    @NotBlank
    @Email
    @Size(min = 5, max = 100)
    private String email;

    @NotBlank
    @Size(min = Constants.PASSWORD_MIN_LENGTH, max = Constants.PASSWORD_MAX_LENGTH)
    private String password;

    @NotBlank
    @Size(min = Constants.PASSWORD_MIN_LENGTH, max = Constants.PASSWORD_MAX_LENGTH)
    private String repeatedPassword;

    @NotBlank
    @Size(min = Constants.FIRSTNAME_MIN_SIZE, message = "Last name length must be greater than 3.")
    private String firstName;

    @NotBlank
    @Size(min = Constants.LASTNAME_MIN_SIZE, message = "Last name length must be greater than 3.")
    private String lastName;
}
