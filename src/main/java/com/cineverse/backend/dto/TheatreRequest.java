package com.cineverse.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TheatreRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Address is required")
        String addressLine,

        @NotNull(message = "City is required")
        Long cityId,

        String pincode,

        Double latitude,

        Double longitude,

        String contactNumber,

        @NotNull(message = "Owner is required")
        Integer ownerId
) {
}
