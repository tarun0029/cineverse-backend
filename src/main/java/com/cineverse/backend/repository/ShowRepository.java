package com.cineverse.backend.repository;

import com.cineverse.backend.entity.Show;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShowRepository extends JpaRepository<Show, UUID> {

    List<Show> findByScreenIdAndShowDate(Long screenId, LocalDate showDate);

    @EntityGraph(attributePaths = {"movie", "language", "screen", "screen.theatre", "seatPricing"})
    List<Show> findByMovie_IdAndShowDateAndScreen_Theatre_City_Id(UUID movieId, LocalDate showDate, Long cityId);

    @EntityGraph(attributePaths = {"movie", "language", "screen", "screen.theatre", "seatPricing"})
    Optional<Show> findWithDetailsById(UUID id);
}
