package com.trading212.weathertrip.domain.dto.hotel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HotelLocationDTO {
    @JsonProperty("dest_id")
    String destId;
    @JsonProperty("city_name")
    String cityName;

    public HotelLocationDTO(@JsonProperty String dest_id, @JsonProperty String city_name) {
        this.destId = dest_id;
        this.cityName = city_name;
    }

    public HotelLocationDTO() {
    }
}
