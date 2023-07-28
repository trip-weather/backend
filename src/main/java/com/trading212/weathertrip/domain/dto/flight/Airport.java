package com.trading212.weathertrip.domain.dto.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Airport {
    @JsonProperty
    private String type;
    @JsonProperty
    private String timeZone;
    @JsonProperty
    private String name;
    @JsonProperty
    private Double longitude;
    @JsonProperty
    private Double latitude;
    @JsonProperty("iata_country_code")
    private String iataCountryCode;
    @JsonProperty("iata_code")
    private String iataCode;
    @JsonProperty("iata_city_code")
    private String iataCityCode;
    @JsonProperty("city_name")
    private String cityName;
}
