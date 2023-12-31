package com.trading212.weathertrip.domain.dto.hotel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WrapperHotelDTO {
    @JsonProperty
    private List<HotelResultDTO> results;

    public WrapperHotelDTO(List<HotelResultDTO> results) {
        this.results = results;
    }
}
