package com.cineverse.backend.dto;

import com.cineverse.backend.entity.enums.ScreenType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScreenRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Screen type is required")
        ScreenType screenType
) {
}
