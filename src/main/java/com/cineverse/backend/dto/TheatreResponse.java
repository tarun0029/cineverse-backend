package com.cineverse.backend.dto;

import com.cineverse.backend.entity.Theatre;

import java.util.UUID;

public record TheatreResponse(
        UUID id,
        String name,
        String addressLine,
        CityResponse city,
        String pincode,
        Double latitude,
        Double longitude,
        String contactNumber,
        Integer ownerId,
        boolean active
) {

    public static TheatreResponse from(Theatre theatre) {
        return new TheatreResponse(
                theatre.getId(),
                theatre.getName(),
                theatre.getAddressLine(),
                new CityResponse(theatre.getCity().getId(), theatre.getCity().getName(), theatre.getCity().getState()),
                theatre.getPincode(),
                theatre.getLatitude(),
                theatre.getLongitude(),
                theatre.getContactNumber(),
                theatre.getOwner().getId(),
                theatre.isActive()
        );
    }
}
