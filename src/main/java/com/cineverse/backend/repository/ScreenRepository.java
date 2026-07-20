package com.cineverse.backend.repository;

import com.cineverse.backend.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScreenRepository extends JpaRepository<Screen, Long> {

    List<Screen> findByTheatreId(UUID theatreId);
}
