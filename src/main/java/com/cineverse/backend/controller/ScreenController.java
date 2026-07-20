package com.cineverse.backend.controller;

import com.cineverse.backend.dto.ScreenRequest;
import com.cineverse.backend.dto.ScreenResponse;
import com.cineverse.backend.service.ScreenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/theatres/{theatreId}/screens")
public class ScreenController {

    private final ScreenService screenService;

    public ScreenController(ScreenService screenService) {
        this.screenService = screenService;
    }

    @GetMapping
    public List<ScreenResponse> list(@PathVariable UUID theatreId) {
        return screenService.listByTheatre(theatreId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ScreenResponse create(@PathVariable UUID theatreId, @Valid @RequestBody ScreenRequest request) {
        return screenService.create(theatreId, request);
    }

    @PutMapping("/{screenId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ScreenResponse update(
            @PathVariable UUID theatreId,
            @PathVariable Long screenId,
            @Valid @RequestBody ScreenRequest request) {
        return screenService.update(theatreId, screenId, request);
    }

    @DeleteMapping("/{screenId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID theatreId, @PathVariable Long screenId) {
        screenService.delete(theatreId, screenId);
    }
}
