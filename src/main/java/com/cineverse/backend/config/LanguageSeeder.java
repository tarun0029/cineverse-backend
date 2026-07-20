package com.cineverse.backend.config;

import com.cineverse.backend.entity.Language;
import com.cineverse.backend.repository.LanguageRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LanguageSeeder implements ApplicationRunner {

    private record LanguageSeed(String name, String code) {
    }

    private static final List<LanguageSeed> DEFAULT_LANGUAGES = List.of(
            new LanguageSeed("English", "en"),
            new LanguageSeed("Hindi", "hi"),
            new LanguageSeed("Tamil", "ta"),
            new LanguageSeed("Telugu", "te"),
            new LanguageSeed("Kannada", "kn"),
            new LanguageSeed("Malayalam", "ml")
    );

    private final LanguageRepository languageRepository;

    public LanguageSeeder(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        DEFAULT_LANGUAGES.forEach(seed -> languageRepository.findByCode(seed.code())
                .orElseGet(() -> languageRepository.save(
                        Language.builder().name(seed.name()).code(seed.code()).build())));
    }
}
