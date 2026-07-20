package com.cineverse.backend.controller;

import com.cineverse.backend.dto.GenreResponse;
import com.cineverse.backend.repository.GenreRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreRepository genreRepository;

    public GenreController(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @GetMapping
    public List<GenreResponse> list() {
        return genreRepository.findAll().stream()
                .map(genre -> new GenreResponse(genre.getId(), genre.getName()))
                .toList();
    }
}
