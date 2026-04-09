package com.GPSolutions.HotelView.repository;

import com.GPSolutions.HotelView.domain.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {

    @Query("select h.brand, count(h) from Hotel h where h.brand is not null group by h.brand")
    List<Object[]> countByBrand();

    @Query("select h.address.city, count(h) from Hotel h where h.address.city is not null group by h.address.city")
    List<Object[]> countByCity();

    @Query("select h.address.country, count(h) from Hotel h where h.address.country is not null group by h.address.country")
    List<Object[]> countByCountry();

    @Query("select a, count(h) from Hotel h join h.amenities a group by a")
    List<Object[]> countByAmenity();
}

