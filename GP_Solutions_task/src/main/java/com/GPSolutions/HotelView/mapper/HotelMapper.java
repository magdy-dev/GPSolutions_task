package com.GPSolutions.HotelView.mapper;

import com.GPSolutions.HotelView.domain.Hotel;
import com.GPSolutions.HotelView.domain.HotelAddress;
import com.GPSolutions.HotelView.domain.HotelArrivalTime;
import com.GPSolutions.HotelView.domain.HotelContacts;
import com.GPSolutions.HotelView.dto.*;

public class HotelMapper {

    private HotelMapper() {
    }

    public static Hotel toEntity(HotelCreateRequestDto dto) {
        Hotel hotel = new Hotel();
        hotel.setName(dto.getName());
        hotel.setDescription(dto.getDescription());
        hotel.setBrand(dto.getBrand());

        HotelAddress address = new HotelAddress();
        address.setHouseNumber(dto.getAddress().getHouseNumber());
        address.setStreet(dto.getAddress().getStreet());
        address.setCity(dto.getAddress().getCity());
        address.setCountry(dto.getAddress().getCountry());
        address.setPostCode(dto.getAddress().getPostCode());
        hotel.setAddress(address);

        HotelContacts contacts = new HotelContacts();
        contacts.setPhone(dto.getContacts().getPhone());
        contacts.setEmail(dto.getContacts().getEmail());
        hotel.setContacts(contacts);

        if (dto.getArrivalTime() != null) {
            HotelArrivalTime arrivalTime = new HotelArrivalTime();
            arrivalTime.setCheckIn(dto.getArrivalTime().getCheckIn());
            arrivalTime.setCheckOut(dto.getArrivalTime().getCheckOut());
            hotel.setArrivalTime(arrivalTime);
        }

        return hotel;
    }

    public static HotelBriefResponseDto toBriefDto(Hotel hotel) {
        HotelBriefResponseDto dto = new HotelBriefResponseDto();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setDescription(hotel.getDescription());
        dto.setPhone(hotel.getContacts() != null ? hotel.getContacts().getPhone() : null);
        if (hotel.getAddress() != null) {
            HotelAddress a = hotel.getAddress();
            String formatted = String.format(
                    "%s %s, %s, %s, %s",
                    a.getHouseNumber(),
                    a.getStreet(),
                    a.getCity(),
                    a.getPostCode(),
                    a.getCountry()
            );
            dto.setAddress(formatted);
        }
        return dto;
    }

    public static HotelDetailResponseDto toDetailDto(Hotel hotel) {
        HotelDetailResponseDto dto = new HotelDetailResponseDto();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setDescription(hotel.getDescription());
        dto.setBrand(hotel.getBrand());

        if (hotel.getAddress() != null) {
            AddressDto addressDto = new AddressDto();
            addressDto.setHouseNumber(hotel.getAddress().getHouseNumber());
            addressDto.setStreet(hotel.getAddress().getStreet());
            addressDto.setCity(hotel.getAddress().getCity());
            addressDto.setCountry(hotel.getAddress().getCountry());
            addressDto.setPostCode(hotel.getAddress().getPostCode());
            dto.setAddress(addressDto);
        }

        if (hotel.getContacts() != null) {
            ContactsDto contactsDto = new ContactsDto();
            contactsDto.setPhone(hotel.getContacts().getPhone());
            contactsDto.setEmail(hotel.getContacts().getEmail());
            dto.setContacts(contactsDto);
        }

        if (hotel.getArrivalTime() != null) {
            ArrivalTimeDto timeDto = new ArrivalTimeDto();
            timeDto.setCheckIn(hotel.getArrivalTime().getCheckIn());
            timeDto.setCheckOut(hotel.getArrivalTime().getCheckOut());
            dto.setArrivalTime(timeDto);
        }

        dto.setAmenities(hotel.getAmenities());
        return dto;
    }
}

