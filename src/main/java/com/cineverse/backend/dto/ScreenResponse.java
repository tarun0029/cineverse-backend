package com.cineverse.backend.dto;

import com.cineverse.backend.entity.Screen;
import com.cineverse.backend.entity.enums.ScreenType;

import java.util.UUID;

public record ScreenResponse(
        Long id,
        UUID theatreId,
        String name,
        ScreenType screenType,
        Integer totalSeats,
        boolean active
) {

    public static ScreenResponse from(Screen screen) {
        return new ScreenResponse(
                screen.getId(),
                screen.getTheatre().getId(),
                screen.getName(),
                screen.getScreenType(),
                screen.getTotalSeats(),
                screen.isActive()
        );
    }
}
