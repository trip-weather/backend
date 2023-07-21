package com.trading212.weathertrip.controllers.validation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import static com.trading212.weathertrip.domain.constants.Constants.FIRSTNAME_MIN_SIZE;
import static com.trading212.weathertrip.domain.constants.Constants.LASTNAME_MIN_SIZE;

@Getter
@Setter
public class UpdateUserValidation {
    @NotNull
    @Size(min = FIRSTNAME_MIN_SIZE)
    private String firstName;

    @NotNull
    @Size(min = LASTNAME_MIN_SIZE)
    private String lastName;
}
