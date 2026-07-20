package com.cineverse.backend.controller;

import com.cineverse.backend.dto.SeatAvailabilityResponse;
import com.cineverse.backend.dto.ShowRequest;
import com.cineverse.backend.dto.ShowResponse;
import com.cineverse.backend.service.ShowService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping
    public List<ShowResponse> search(
            @RequestParam UUID movieId,
            @RequestParam Long cityId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate showDate) {
        return showService.search(movieId, cityId, showDate);
    }

    @GetMapping("/{id}")
    public ShowResponse getById(@PathVariable UUID id) {
        return showService.getById(id);
    }

    @GetMapping("/{id}/seats")
    public List<SeatAvailabilityResponse> getSeatAvailability(@PathVariable UUID id) {
        return showService.getSeatAvailability(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ShowResponse create(@Valid @RequestBody ShowRequest request) {
        return showService.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void cancel(@PathVariable UUID id) {
        showService.cancel(id);
    }
}
