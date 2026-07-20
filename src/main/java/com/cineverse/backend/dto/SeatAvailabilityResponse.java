package com.cineverse.backend.dto;

import com.cineverse.backend.entity.enums.SeatType;

import java.math.BigDecimal;

public record SeatAvailabilityResponse(
        Long seatId,
        String rowLabel,
        Integer seatNumber,
        SeatType seatType,
        BigDecimal price,
        boolean booked
) {
}
