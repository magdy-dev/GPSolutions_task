package com.GPSolutions.HotelView.controller;

import com.GPSolutions.HotelView.dto.HotelBriefResponseDto;
import com.GPSolutions.HotelView.dto.HotelCreateRequestDto;
import com.GPSolutions.HotelView.dto.HotelDetailResponseDto;
import com.GPSolutions.HotelView.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/property-view")
@Tag(name = "Hotels", description = "Hotel management APIs")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping(value = "/hotels", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all hotels", description = "Returns a list of all hotels with brief information")
    public List<HotelBriefResponseDto> getHotels() {
        return hotelService.getAllHotels();
    }

    @GetMapping(value = "/hotels/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get hotel by id", description = "Returns detailed information about a specific hotel")
    public HotelDetailResponseDto getHotel(@PathVariable("id") Long id) {
        return hotelService.getHotel(id);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Search hotels", description = "Search hotels by name, brand, city, country, and amenities")
    public List<HotelBriefResponseDto> searchHotels(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "brand", required = false) String brand,
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "country", required = false) String country,
            @RequestParam(name = "amenities", required = false) List<String> amenities
    ) {
        return hotelService.searchHotels(name, brand, city, country, amenities);
    }

    @PostMapping(value = "/hotels", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create hotel", description = "Creates a new hotel")
    public ResponseEntity<HotelBriefResponseDto> createHotel(@Valid @RequestBody HotelCreateRequestDto request) {
        HotelBriefResponseDto created = hotelService.createHotel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping(value = "/hotels/{id}/amenities", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update amenities", description = "Adds or replaces amenities of a hotel")
    public HotelDetailResponseDto updateAmenities(@PathVariable("id") Long id, @RequestBody List<String> amenities) {
        return hotelService.updateAmenities(id, amenities);
    }

    @GetMapping(value = "/histogram/{param}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Histogram", description = "Returns a histogram of hotels grouped by the given parameter (brand, city, country, amenities)")
    public Map<String, Long> histogram(@PathVariable("param") String param) {
        return hotelService.histogram(param);
    }
}

