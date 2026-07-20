package com.cineverse.backend.config;

import com.cineverse.backend.entity.Genre;
import com.cineverse.backend.repository.GenreRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GenreSeeder implements ApplicationRunner {

    private static final List<String> DEFAULT_GENRES = List.of(
            "Action", "Comedy", "Drama", "Thriller", "Horror", "Romance", "Sci-Fi", "Animation"
    );

    private final GenreRepository genreRepository;

    public GenreSeeder(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        DEFAULT_GENRES.forEach(name -> genreRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> genreRepository.save(Genre.builder().name(name).build())));
    }
}
