package com.cineverse.backend.repository;

import com.cineverse.backend.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findByActiveTrueOrderByNameAsc();

    List<City> findByNameContainingIgnoreCase(String name);

    Optional<City> findByNameIgnoreCaseAndStateIgnoreCase(String name, String state);
}
