package com.trading212.weathertrip.controllers.validation;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchHotelValidation {
    private String city;
    private String minTemp;
    private String maxTemp;
    private Integer period;
    private List<String> filters;
}
