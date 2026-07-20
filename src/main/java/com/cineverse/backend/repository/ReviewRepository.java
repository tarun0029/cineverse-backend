package com.cineverse.backend.repository;

import com.cineverse.backend.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByMovieId(UUID movieId, Pageable pageable);

    Optional<Review> findByUserIdAndMovieId(UUID userId, UUID movieId);

    long countByMovieId(UUID movieId);

    @Query("select avg(r.rating) from Review r where r.movie.id = :movieId")
    Double findAverageRatingByMovieId(@Param("movieId") UUID movieId);
}
