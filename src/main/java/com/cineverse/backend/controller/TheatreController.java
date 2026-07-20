package com.cineverse.backend.controller;

import com.cineverse.backend.dto.TheatreRequest;
import com.cineverse.backend.dto.TheatreResponse;
import com.cineverse.backend.service.TheatreService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/theatres")
public class TheatreController {

    private final TheatreService theatreService;

    public TheatreController(TheatreService theatreService) {
        this.theatreService = theatreService;
    }

    @GetMapping
    public Page<TheatreResponse> search(
            @RequestParam(required = false) Long cityId,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return theatreService.search(cityId, pageable);
    }

    @GetMapping("/{id}")
    public TheatreResponse getById(@PathVariable UUID id) {
        return theatreService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public TheatreResponse create(@Valid @RequestBody TheatreRequest request) {
        return theatreService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TheatreResponse update(@PathVariable UUID id, @Valid @RequestBody TheatreRequest request) {
        return theatreService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        theatreService.delete(id);
    }
}
