package com.cineverse.backend.controller;

import com.cineverse.backend.dto.CityResponse;
import com.cineverse.backend.repository.CityRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityRepository cityRepository;

    public CityController(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @GetMapping
    public List<CityResponse> list() {
        return cityRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(city -> new CityResponse(city.getId(), city.getName(), city.getState()))
                .toList();
    }
}
