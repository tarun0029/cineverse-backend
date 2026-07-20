package com.cineverse.backend.config;

import com.cineverse.backend.entity.City;
import com.cineverse.backend.repository.CityRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CitySeeder implements ApplicationRunner {

    private record CitySeed(String name, String state) {
    }

    private static final List<CitySeed> DEFAULT_CITIES = List.of(
            new CitySeed("Mumbai", "Maharashtra"),
            new CitySeed("Delhi", "Delhi"),
            new CitySeed("Bengaluru", "Karnataka"),
            new CitySeed("Chennai", "Tamil Nadu"),
            new CitySeed("Hyderabad", "Telangana"),
            new CitySeed("Kolkata", "West Bengal"),
            new CitySeed("Pune", "Maharashtra"),
            new CitySeed("Ahmedabad", "Gujarat")
    );

    private final CityRepository cityRepository;

    public CitySeeder(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        DEFAULT_CITIES.forEach(seed -> cityRepository.findByNameIgnoreCaseAndStateIgnoreCase(seed.name(), seed.state())
                .orElseGet(() -> cityRepository.save(City.builder().name(seed.name()).state(seed.state()).build())));
    }
}
