package com.GPSolutions.HotelView.controller;

import com.GPSolutions.HotelView.domain.Hotel;
import com.GPSolutions.HotelView.domain.HotelAddress;
import com.GPSolutions.HotelView.domain.HotelContacts;
import com.GPSolutions.HotelView.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HotelControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HotelRepository hotelRepository;

    @BeforeEach
    void setup() {
        hotelRepository.deleteAll();
        Hotel h = new Hotel();
        h.setName("DoubleTree by Hilton Minsk");
        h.setDescription("The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms...");
        h.setBrand("Hilton");
        HotelAddress a = new HotelAddress();
        a.setHouseNumber("9");
        a.setStreet("Pobediteley Avenue");
        a.setCity("Minsk");
        a.setCountry("Belarus");
        a.setPostCode("220004");
        h.setAddress(a);
        HotelContacts c = new HotelContacts();
        c.setPhone("+375 17 309-80-00");
        c.setEmail("doubletreeminsk.info@hilton.com");
        h.setContacts(c);
        h.setAmenities(Set.of("Free parking", "Free WiFi"));
        hotelRepository.save(h);
    }

    @Test
    void getHotels_returnsList() throws Exception {
        mockMvc.perform(get("/property-view/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("DoubleTree by Hilton Minsk")));
    }

    @Test
    void getHotelById_returnsDetail() throws Exception {
        Long id = hotelRepository.findAll().get(0).getId();
        mockMvc.perform(get("/property-view/hotels/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$.brand", is("Hilton")))
                .andExpect(jsonPath("$.address.city", is("Minsk")));
    }

    @Test
    void searchByCity_returnsResult() throws Exception {
        mockMvc.perform(get("/property-view/search").param("city", "Minsk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchByAmenity_returnsResult() throws Exception {
        mockMvc.perform(get("/property-view/search").param("amenities", "Free WiFi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void createHotel_validationError() throws Exception {
        String body = """
                {
                  "description": "Missing name"
                }
                """;
        mockMvc.perform(post("/property-view/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void histogramCity_returnsMap() throws Exception {
        mockMvc.perform(get("/property-view/histogram/city"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Minsk", is(1)));
    }

    @Test
    void histogramAmenities_returnsMap() throws Exception {
        mockMvc.perform(get("/property-view/histogram/amenities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Free WiFi']", is(1)));
    }

    @Test
    void getHotelById_notFound() throws Exception {
        mockMvc.perform(get("/property-view/hotels/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Hotel not found with id: 999999")));
    }

    @Test
    void histogram_invalidParam_returns400() throws Exception {
        mockMvc.perform(get("/property-view/histogram/unknown"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createHotel_success_returnsCreatedBrief() throws Exception {
        String body = """
                {
                  "name": "New Hotel",
                  "description": "Some description",
                  "brand": "BrandX",
                  "address": {
                    "houseNumber": "10",
                    "street": "Main Street",
                    "city": "Minsk",
                    "country": "Belarus",
                    "postCode": "220000"
                  },
                  "contacts": {
                    "phone": "+1 111 111",
                    "email": "new@hotel.com"
                  },
                  "arrivalTime": {
                    "checkIn": "14:00",
                    "checkOut": "12:00"
                  }
                }
                """;

        mockMvc.perform(post("/property-view/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("New Hotel")))
                .andExpect(jsonPath("$.phone", is("+1 111 111")));
    }

    @Test
    void updateAmenities_replacesAmenities() throws Exception {
        Long id = hotelRepository.findAll().get(0).getId();
        String body = """
                ["Concierge","Fitness center"]
                """;

        mockMvc.perform(post("/property-view/hotels/{id}/amenities", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$.amenities", hasSize(2)));
    }
}

