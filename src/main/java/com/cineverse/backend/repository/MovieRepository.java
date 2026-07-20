package com.cineverse.backend.repository;

import com.cineverse.backend.entity.Movie;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface MovieRepository extends JpaRepository<Movie, UUID>, JpaSpecificationExecutor<Movie> {

    @EntityGraph(attributePaths = {"genres", "languages"})
    Optional<Movie> findWithGenresAndLanguagesById(UUID id);
}
