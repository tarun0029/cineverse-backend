package com.cineverse.backend.service;

import com.cineverse.backend.dto.MovieRequest;
import com.cineverse.backend.dto.MovieResponse;
import com.cineverse.backend.entity.Genre;
import com.cineverse.backend.entity.Language;
import com.cineverse.backend.entity.Movie;
import com.cineverse.backend.entity.enums.MovieStatus;
import com.cineverse.backend.exception.InvalidReferenceException;
import com.cineverse.backend.exception.ResourceNotFoundException;
import com.cineverse.backend.repository.GenreRepository;
import com.cineverse.backend.repository.LanguageRepository;
import com.cineverse.backend.repository.MovieRepository;
import com.cineverse.backend.specification.MovieSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final LanguageRepository languageRepository;

    public MovieService(MovieRepository movieRepository, GenreRepository genreRepository, LanguageRepository languageRepository) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.languageRepository = languageRepository;
    }

    @Transactional(readOnly = true)
    public Page<MovieResponse> search(String title, MovieStatus status, Long genreId, Long languageId, Pageable pageable) {
        Specification<Movie> spec = Specification.where(MovieSpecifications.isActive(true))
                .and(MovieSpecifications.titleContains(title))
                .and(MovieSpecifications.hasStatus(status))
                .and(MovieSpecifications.hasGenre(genreId))
                .and(MovieSpecifications.hasLanguage(languageId));

        return movieRepository.findAll(spec, pageable).map(MovieResponse::from);
    }

    @Transactional(readOnly = true)
    public MovieResponse getById(UUID id) {
        Movie movie = movieRepository.findWithGenresAndLanguagesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id " + id));
        return MovieResponse.from(movie);
    }

    @Transactional
    public MovieResponse create(MovieRequest request) {
        Movie movie = Movie.builder()
                .title(request.title())
                .description(request.description())
                .durationMinutes(request.durationMinutes())
                .releaseDate(request.releaseDate())
                .censorRating(request.censorRating())
                .status(request.status() != null ? request.status() : MovieStatus.UPCOMING)
                .posterUrl(request.posterUrl())
                .bannerUrl(request.bannerUrl())
                .genres(resolveGenres(request.genreIds()))
                .languages(resolveLanguages(request.languageIds()))
                .build();

        movieRepository.save(movie);
        return MovieResponse.from(movie);
    }

    @Transactional
    public MovieResponse update(UUID id, MovieRequest request) {
        Movie movie = movieRepository.findWithGenresAndLanguagesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id " + id));

        movie.setTitle(request.title());
        movie.setDescription(request.description());
        movie.setDurationMinutes(request.durationMinutes());
        movie.setReleaseDate(request.releaseDate());
        movie.setCensorRating(request.censorRating());
        if (request.status() != null) {
            movie.setStatus(request.status());
        }
        movie.setPosterUrl(request.posterUrl());
        movie.setBannerUrl(request.bannerUrl());
        movie.setGenres(resolveGenres(request.genreIds()));
        movie.setLanguages(resolveLanguages(request.languageIds()));

        return MovieResponse.from(movie);
    }

    @Transactional
    public void delete(UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id " + id));
        movie.setActive(false);
    }

    private Set<Genre> resolveGenres(Set<Long> genreIds) {
        List<Genre> found = genreRepository.findAllById(genreIds);
        if (found.size() != genreIds.size()) {
            throw new InvalidReferenceException("One or more genre IDs do not exist");
        }
        return new HashSet<>(found);
    }

    private Set<Language> resolveLanguages(Set<Long> languageIds) {
        List<Language> found = languageRepository.findAllById(languageIds);
        if (found.size() != languageIds.size()) {
            throw new InvalidReferenceException("One or more language IDs do not exist");
        }
        return new HashSet<>(found);
    }
}
