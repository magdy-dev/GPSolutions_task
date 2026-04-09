package com.GPSolutions.HotelView.service;

import com.GPSolutions.HotelView.domain.Hotel;
import com.GPSolutions.HotelView.dto.HotelBriefResponseDto;
import com.GPSolutions.HotelView.dto.HotelCreateRequestDto;
import com.GPSolutions.HotelView.dto.HotelDetailResponseDto;
import com.GPSolutions.HotelView.exception.HotelNotFoundException;
import com.GPSolutions.HotelView.mapper.HotelMapper;
import com.GPSolutions.HotelView.repository.HotelRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class HotelService {

    private static final Logger log = LoggerFactory.getLogger(HotelService.class);

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public List<HotelBriefResponseDto> getAllHotels() {
        log.info("Fetching all hotels");
        return hotelRepository.findAll()
                .stream()
                .map(HotelMapper::toBriefDto)
                .toList();
    }

    public HotelDetailResponseDto getHotel(Long id) {
        log.info("Fetching hotel details by id={}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
        return HotelMapper.toDetailDto(hotel);
    }

    public List<HotelBriefResponseDto> searchHotels(String name,
                                                    String brand,
                                                    String city,
                                                    String country,
                                                    List<String> amenities) {
        log.info("Searching hotels with filters name={}, brand={}, city={}, country={}, amenities={}",
                name, brand, city, country, amenities);

        Specification<Hotel> spec = Specification.where(null);

        if (StringUtils.hasText(name)) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (StringUtils.hasText(brand)) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("brand")), "%" + brand.toLowerCase() + "%"));
        }
        if (StringUtils.hasText(city)) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("address").get("city")), "%" + city.toLowerCase() + "%"));
        }
        if (StringUtils.hasText(country)) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("address").get("country")), "%" + country.toLowerCase() + "%"));
        }

        List<Hotel> hotels = spec == null ? hotelRepository.findAll() : hotelRepository.findAll(spec);

        if (!CollectionUtils.isEmpty(amenities)) {
            Set<String> required = amenities.stream()
                    .filter(StringUtils::hasText)
                    .map(a -> a.toLowerCase(Locale.ROOT))
                    .collect(Collectors.toSet());

            hotels = hotels.stream()
                    .filter(h -> {
                        Set<String> hotelAmenities = h.getAmenities().stream()
                                .map(a -> a.toLowerCase(Locale.ROOT))
                                .collect(Collectors.toSet());
                        return hotelAmenities.containsAll(required);
                    })
                    .toList();
        }

        return hotels.stream()
                .map(HotelMapper::toBriefDto)
                .toList();
    }

    public HotelBriefResponseDto createHotel(HotelCreateRequestDto request) {
        log.info("Creating hotel name={} brand={}", request.getName(), request.getBrand());
        Hotel hotel = HotelMapper.toEntity(request);
        Hotel saved = hotelRepository.save(hotel);
        return HotelMapper.toBriefDto(saved);
    }

    public HotelDetailResponseDto updateAmenities(Long hotelId, List<String> amenities) {
        log.info("Updating amenities for hotel id={}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));

        Set<String> newAmenities = amenities == null ? Collections.emptySet() :
                amenities.stream()
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toCollection(LinkedHashSet::new));

        hotel.setAmenities(newAmenities);
        Hotel saved = hotelRepository.save(hotel);
        return HotelMapper.toDetailDto(saved);
    }

    public Map<String, Long> histogram(String param) {
        log.info("Building histogram by param={}", param);
        return switch (param.toLowerCase(Locale.ROOT)) {
            case "brand" -> toMap(hotelRepository.countByBrand());
            case "city" -> toMap(hotelRepository.countByCity());
            case "country" -> toMap(hotelRepository.countByCountry());
            case "amenities" -> toMap(hotelRepository.countByAmenity());
            default -> throw new IllegalArgumentException("Unsupported histogram parameter: " + param);
        };
    }

    private Map<String, Long> toMap(List<Object[]> tuples) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] t : tuples) {
            String key = Objects.toString(t[0], null);
            Long count = t[1] instanceof Number n ? n.longValue() : 0L;
            if (key != null) {
                result.put(key, count);
            }
        }
        return result;
    }
}

