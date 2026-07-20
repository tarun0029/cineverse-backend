package com.cineverse.backend.dto;

import com.cineverse.backend.entity.enums.CensorRating;
import com.cineverse.backend.entity.enums.MovieStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.Set;

public record MovieRequest(

        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotNull(message = "Duration is required")
        @Positive(message = "Duration must be a positive number of minutes")
        Integer durationMinutes,

        @NotNull(message = "Release date is required")
        LocalDate releaseDate,

        @NotNull(message = "Censor rating is required")
        CensorRating censorRating,

        MovieStatus status,

        String posterUrl,

        String bannerUrl,

        @NotEmpty(message = "At least one genre is required")
        Set<Long> genreIds,

        @NotEmpty(message = "At least one language is required")
        Set<Long> languageIds
) {
}
