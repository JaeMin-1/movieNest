package com.movie.movienest.global.util;

import com.movie.movienest.domain.movie.dto.response.MovieSearchResponse;

import java.util.Comparator;

public class MovieSortUtil {
    public static Comparator<MovieSearchResponse.MovieSummary> getComparator(String sort) {
        return switch (sort) {
            case "rating" -> Comparator.comparing(MovieSearchResponse.MovieSummary::getAverageRating).reversed();
            case "review" -> Comparator.comparing(MovieSearchResponse.MovieSummary::getReviewCount).reversed();
            default -> Comparator.comparing(MovieSearchResponse.MovieSummary::getReleaseDate);
        };
    }
}
