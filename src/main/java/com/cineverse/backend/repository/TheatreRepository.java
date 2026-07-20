package com.cineverse.backend.repository;

import com.cineverse.backend.entity.Theatre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TheatreRepository extends JpaRepository<Theatre, UUID> {

    @EntityGraph(attributePaths = {"city", "owner"})
    Page<Theatre> findByCityIdAndActiveTrue(Long cityId, Pageable pageable);

    @EntityGraph(attributePaths = {"city", "owner"})
    Page<Theatre> findByActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"city", "owner"})
    Optional<Theatre> findById(UUID id);

    List<Theatre> findByOwnerId(Integer ownerId);
}
