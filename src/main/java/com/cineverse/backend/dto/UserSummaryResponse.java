package com.cineverse.backend.dto;

import com.cineverse.backend.entity.Role;
import com.cineverse.backend.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

public record UserSummaryResponse(Integer id, String name, String email, String mobileNumber, Set<String> roles) {

    public static UserSummaryResponse from(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        return new UserSummaryResponse(user.getId(), user.getName(), user.getEmail(), user.getMobileNumber(), roleNames);
    }
}
