package com.cineverse.backend.dto;

import com.cineverse.backend.entity.enums.SeatType;
import com.cineverse.backend.entity.enums.ShowFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record ShowRequest(

        @NotNull(message = "Movie is required")
        UUID movieId,

        @NotNull(message = "Screen is required")
        Long screenId,

        @NotNull(message = "Language is required")
        Long languageId,

        @NotNull(message = "Format is required")
        ShowFormat format,

        @NotNull(message = "Show date is required")
        LocalDate showDate,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NotEmpty(message = "At least one seat pricing entry is required")
        @Valid
        List<SeatPricingRequest> pricing
) {

    public record SeatPricingRequest(

            @NotNull(message = "Seat type is required")
            SeatType seatType,

            @NotNull(message = "Price is required")
            @Positive(message = "Price must be positive")
            BigDecimal price
    ) {
    }
}
