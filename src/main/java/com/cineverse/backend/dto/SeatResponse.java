package com.cineverse.backend.dto;

import com.cineverse.backend.entity.Seat;
import com.cineverse.backend.entity.enums.SeatType;

public record SeatResponse(Long id, String rowLabel, Integer seatNumber, SeatType seatType) {

    public static SeatResponse from(Seat seat) {
        return new SeatResponse(seat.getId(), seat.getRowLabel(), seat.getSeatNumber(), seat.getSeatType());
    }
}
