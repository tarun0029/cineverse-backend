package com.cineverse.backend.repository;

import com.cineverse.backend.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TheatreRepository extends JpaRepository<Theatre, UUID> {

    List<Theatre> findByCityIdAndActiveTrue(Long cityId);

    List<Theatre> findByOwnerId(Integer ownerId);
}
