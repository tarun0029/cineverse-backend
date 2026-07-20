package com.cineverse.backend.controller;

import com.cineverse.backend.dto.LanguageResponse;
import com.cineverse.backend.repository.LanguageRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/languages")
public class LanguageController {

    private final LanguageRepository languageRepository;

    public LanguageController(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    @GetMapping
    public List<LanguageResponse> list() {
        return languageRepository.findAll().stream()
                .map(language -> new LanguageResponse(language.getId(), language.getName(), language.getCode()))
                .toList();
    }
}
