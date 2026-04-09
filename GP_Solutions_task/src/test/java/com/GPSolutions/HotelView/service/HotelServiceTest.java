package com.GPSolutions.HotelView.service;

import com.GPSolutions.HotelView.domain.Hotel;
import com.GPSolutions.HotelView.domain.HotelAddress;
import com.GPSolutions.HotelView.domain.HotelContacts;
import com.GPSolutions.HotelView.dto.AddressDto;
import com.GPSolutions.HotelView.dto.ContactsDto;
import com.GPSolutions.HotelView.dto.HotelCreateRequestDto;
import com.GPSolutions.HotelView.exception.HotelNotFoundException;
import com.GPSolutions.HotelView.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HotelServiceTest {

    private HotelRepository hotelRepository;
    private HotelService hotelService;

    @BeforeEach
    void setUp() {
        hotelRepository = mock(HotelRepository.class);
        hotelService = new HotelService(hotelRepository);
    }

    @Test
    void getHotel_existing_returnsDetail() {
        Hotel hotel = sampleHotel(1L);
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        var dto = hotelService.getHotel(1L);

        assertEquals(1L, dto.getId());
        assertEquals("Test Hotel", dto.getName());
    }

    @Test
    void getHotel_notExisting_throws() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(HotelNotFoundException.class, () -> hotelService.getHotel(1L));
    }

    @Test
    void createHotel_savesAndReturnsBrief() {
        HotelCreateRequestDto req = new HotelCreateRequestDto();
        req.setName("Test Hotel");
        req.setDescription("Description");
        AddressDto ad = new AddressDto();
        ad.setHouseNumber("1");
        ad.setStreet("Main");
        ad.setCity("City");
        ad.setCountry("Country");
        ad.setPostCode("0000");
        req.setAddress(ad);
        ContactsDto cd = new ContactsDto();
        cd.setPhone("123");
        cd.setEmail("a@b.com");
        req.setContacts(cd);

        Hotel saved = sampleHotel(10L);
        when(hotelRepository.save(ArgumentMatchers.any(Hotel.class))).thenReturn(saved);

        var dto = hotelService.createHotel(req);

        assertEquals(10L, dto.getId());
        assertEquals("Test Hotel", dto.getName());
        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }

    @Test
    void updateAmenities_replacesList() {
        Hotel hotel = sampleHotel(1L);
        hotel.setAmenities(Set.of("Old"));
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(any(Hotel.class))).thenAnswer(i -> i.getArgument(0));

        var dto = hotelService.updateAmenities(1L, List.of("A", "B"));

        assertTrue(dto.getAmenities().contains("A"));
        assertFalse(dto.getAmenities().contains("Old"));
    }

    @Test
    void histogram_brand_usesRepository() {
        when(hotelRepository.countByBrand()).thenReturn(List.<Object[]>of(new Object[]{"Hilton", 2L}));
        Map<String, Long> map = hotelService.histogram("brand");
        assertEquals(1, map.size());
        assertEquals(2L, map.get("Hilton"));
    }

    private Hotel sampleHotel(Long id) {
        Hotel h = new Hotel();
        h.setId(id);
        h.setName("Test Hotel");
        h.setDescription("Desc");
        HotelAddress address = new HotelAddress();
        address.setHouseNumber("1");
        address.setStreet("Main");
        address.setCity("City");
        address.setCountry("Country");
        address.setPostCode("0000");
        h.setAddress(address);
        HotelContacts contacts = new HotelContacts();
        contacts.setPhone("123");
        contacts.setEmail("a@b.com");
        h.setContacts(contacts);
        return h;
    }
}

