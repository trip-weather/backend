package com.trading212.weathertrip.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GooglePlacesWrapperDTO {
    private List<GooglePlacesResultDTO> results;
}
