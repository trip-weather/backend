package com.trading212.weathertrip.domain.dto.hotel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HotelLocationDTO {
    @JsonProperty(namespace = "dest_id")
    String dest_id;
    @JsonProperty()
    String city_name;

    public HotelLocationDTO(@JsonProperty String dest_id, @JsonProperty String city_name) {
        this.dest_id = dest_id;
        this.city_name = city_name;
    }

    public HotelLocationDTO() {
    }
}
