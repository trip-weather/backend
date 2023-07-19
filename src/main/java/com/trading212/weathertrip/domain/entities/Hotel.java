package com.trading212.weathertrip.domain.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Hotel {
    private String uuid;
    private int externalId;
    private String name;
    String provider;
    private int favouriteCount;

}
