package com.cineverse.backend.dto;

import com.cineverse.backend.entity.enums.SeatType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record SeatLayoutRequest(

        @NotEmpty(message = "At least one row is required")
        @Valid
        List<SeatRowRequest> rows
) {

    public record SeatRowRequest(

            @NotBlank(message = "Row label is required")
            String rowLabel,

            @NotNull(message = "Number of seats is required")
            @Positive(message = "Number of seats must be positive")
            Integer numberOfSeats,

            @NotNull(message = "Seat type is required")
            SeatType seatType
    ) {
    }
}
