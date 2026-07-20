package com.cineverse.backend.repository;

import com.cineverse.backend.entity.ShowSeatPricing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShowSeatPricingRepository extends JpaRepository<ShowSeatPricing, Long> {

    List<ShowSeatPricing> findByShowId(UUID showId);
}
