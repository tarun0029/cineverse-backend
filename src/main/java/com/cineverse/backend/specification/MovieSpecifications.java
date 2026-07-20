package com.cineverse.backend.specification;

import com.cineverse.backend.entity.Genre;
import com.cineverse.backend.entity.Language;
import com.cineverse.backend.entity.Movie;
import com.cineverse.backend.entity.enums.MovieStatus;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public final class MovieSpecifications {

    private MovieSpecifications() {
    }

    public static Specification<Movie> isActive(boolean active) {
        return (root, query, cb) -> cb.equal(root.get("active"), active);
    }

    public static Specification<Movie> titleContains(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Movie> hasStatus(MovieStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Movie> hasGenre(Long genreId) {
        return (root, query, cb) -> {
            if (genreId == null) {
                return null;
            }
            query.distinct(true);
            Join<Movie, Genre> genres = root.join("genres");
            return cb.equal(genres.get("id"), genreId);
        };
    }

    public static Specification<Movie> hasLanguage(Long languageId) {
        return (root, query, cb) -> {
            if (languageId == null) {
                return null;
            }
            query.distinct(true);
            Join<Movie, Language> languages = root.join("languages");
            return cb.equal(languages.get("id"), languageId);
        };
    }
}
