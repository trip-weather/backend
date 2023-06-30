package com.trading212.weathertrip.controllers.validation;

import com.trading212.weathertrip.domain.constants.Constants;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordAndKeyValidation {
    private String key;

    @Size(min = Constants.PASSWORD_MIN_LENGTH, max = Constants.PASSWORD_MAX_LENGTH)
    private String newPassword;
}
