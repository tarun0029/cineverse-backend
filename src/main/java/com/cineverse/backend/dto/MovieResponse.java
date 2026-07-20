package com.cineverse.backend.dto;

import com.cineverse.backend.entity.Movie;
import com.cineverse.backend.entity.enums.CensorRating;
import com.cineverse.backend.entity.enums.MovieStatus;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record MovieResponse(
        UUID id,
        String title,
        String description,
        Integer durationMinutes,
        LocalDate releaseDate,
        CensorRating censorRating,
        MovieStatus status,
        String posterUrl,
        String bannerUrl,
        Double averageRating,
        Long totalVotes,
        boolean active,
        Set<GenreResponse> genres,
        Set<LanguageResponse> languages
) {

    public static MovieResponse from(Movie movie) {
        Set<GenreResponse> genres = movie.getGenres().stream()
                .map(genre -> new GenreResponse(genre.getId(), genre.getName()))
                .collect(Collectors.toSet());
        Set<LanguageResponse> languages = movie.getLanguages().stream()
                .map(language -> new LanguageResponse(language.getId(), language.getName(), language.getCode()))
                .collect(Collectors.toSet());

        return new MovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getDurationMinutes(),
                movie.getReleaseDate(),
                movie.getCensorRating(),
                movie.getStatus(),
                movie.getPosterUrl(),
                movie.getBannerUrl(),
                movie.getAverageRating(),
                movie.getTotalVotes(),
                movie.isActive(),
                genres,
                languages
        );
    }
}
