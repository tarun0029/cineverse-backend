package com.cineverse.backend.repository;

import com.cineverse.backend.entity.Screen;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScreenRepository extends JpaRepository<Screen, Long> {

    @EntityGraph(attributePaths = "theatre")
    List<Screen> findByTheatreId(UUID theatreId);

    @EntityGraph(attributePaths = "theatre")
    Optional<Screen> findById(Long id);
}
