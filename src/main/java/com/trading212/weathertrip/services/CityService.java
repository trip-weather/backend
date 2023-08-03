package com.trading212.weathertrip.services;

import com.trading212.weathertrip.domain.constants.Constants;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CityService {
    public List<String> getCitiesByType(String type) {
        if (type.equals("summer")) {
            return Constants.GET_SUMMER_CITIES;
        } else if (type.equals("winter")) {
            return Constants.GET_WINTER_CITIES;
        }
        return Collections.emptyList();
    }
}
