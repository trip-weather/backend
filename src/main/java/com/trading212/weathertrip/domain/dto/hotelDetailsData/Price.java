package com.trading212.weathertrip.domain.dto.hotelDetailsData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Price {
    @JsonProperty("gross_amount_per_night")
    private PricePerNight pricePerNight;
}
