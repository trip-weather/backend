package com.trading212.weathertrip.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading212.weathertrip.domain.dto.GooglePlacesResultDTO;
import com.trading212.weathertrip.domain.dto.GooglePlacesWrapperDTO;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.trading212.weathertrip.domain.constants.APIConstants.GOOGLE_MAPS_API_KEY;
import static com.trading212.weathertrip.domain.constants.APIConstants.GOOGLE_MAPS_NEARBY_SEARCH;

@Service
public class GoogleMapsService {
    private static final Integer RADIUS = 1000;
    private final ObjectMapper objectMapper;

    public GoogleMapsService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<GooglePlacesResultDTO> findNearest(String latitude, String longitude, String keyword) {
        String url = GOOGLE_MAPS_NEARBY_SEARCH +
                "?keyword=" + keyword
                + "&location=" + latitude + "," + longitude
                + "&radius=" + RADIUS
                + "&type=museum" + keyword
                + "rankby=distance"
                + "&key=" + GOOGLE_MAPS_API_KEY;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();

            GooglePlacesWrapperDTO googlePlacesWrapperDTO = objectMapper.readValue(response.body().string(), GooglePlacesWrapperDTO.class);

            return googlePlacesWrapperDTO.getResults();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
