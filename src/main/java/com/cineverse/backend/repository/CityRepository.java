package com.cineverse.backend.repository;

import com.cineverse.backend.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findByActiveTrueOrderByNameAsc();

    List<City> findByNameContainingIgnoreCase(String name);
}
