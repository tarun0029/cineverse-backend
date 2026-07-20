package com.cineverse.backend.repository;

import com.cineverse.backend.entity.Movie;
import com.cineverse.backend.entity.enums.MovieStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MovieRepository extends JpaRepository<Movie, UUID> {

    Page<Movie> findByStatus(MovieStatus status, Pageable pageable);

    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @EntityGraph(attributePaths = {"genres", "languages"})
    Optional<Movie> findWithGenresAndLanguagesById(UUID id);
}
