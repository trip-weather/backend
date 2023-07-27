package com.trading212.weathertrip.controllers;

import com.trading212.weathertrip.domain.dto.hotel.HotelResultDTO;
import com.trading212.weathertrip.domain.dto.hotel.WrapperHotelDTO;
import com.trading212.weathertrip.services.SearchService;
import com.trading212.weathertrip.services.hotel.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SearchController {
    private final SearchService searchService;
    private final HotelService hotelService;

    public SearchController(SearchService vocationService,
                            HotelService hotelService) {

        this.searchService = vocationService;
        this.hotelService = hotelService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<WrapperHotelDTO>> search(@RequestParam(value = "city", required = false) String city,
                                                        @RequestParam(value = "minTemp", required = false) String minTemp,
                                                        @RequestParam(value = "maxTemp", required = false) String maxTemp,
                                                        @RequestParam(value = "period", required = false) Integer period
    ) throws IOException {
        List<WrapperHotelDTO> vocations = searchService.search(city, minTemp, maxTemp, period);
        hotelService.save(vocations);
        return ResponseEntity.ok(vocations);
    }

    @GetMapping("/hotels/suggested")
    public ResponseEntity<List<HotelResultDTO>> suggestedHotels() throws IOException {
        List<HotelResultDTO> suggestedHotels = hotelService.getHotels();
        hotelService.save(List.of(new WrapperHotelDTO(suggestedHotels)));
        return ResponseEntity.ok(suggestedHotels);
    }

    @GetMapping("/hotels/suggested-by-location")
    public ResponseEntity<List<WrapperHotelDTO>> locationHotels(@RequestParam(value = "city") String city) throws IOException {
        List<WrapperHotelDTO> hotelsByLocation = searchService.search(city, null, null, null);
        hotelService.save(hotelsByLocation);
        return ResponseEntity.ok(hotelsByLocation);
    }

    @GetMapping("/get/cities")
    public ResponseEntity<List<String>> getCities() {
        return ResponseEntity.ok(searchService.getCities());
    }
}
