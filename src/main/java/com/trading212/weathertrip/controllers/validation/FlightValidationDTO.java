package com.trading212.weathertrip.controllers.validation;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightValidationDTO {
    @NotEmpty
    private String origin;

    @NotEmpty
    private String destination;

    @NotEmpty
    private String departDate;

    @NotEmpty
    private String adults;

    private String returnDate;
}
