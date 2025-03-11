package com.movie.movienest.domain.movie.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.movie.movienest.domain.movie.entity.Movie;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.movie.movienest.global.constants.Constant.TMDB_IMAGE_BASE_URL;

@Getter
@Builder
public class MovieSearchResponse {

    @JsonProperty("results")
    private List<MovieSummary> movies;

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode(of = "id")
    public static class MovieSummary {
        private Long id;
        private String title;
        private Double averageRating;

        @JsonProperty("release_date")
        private String releaseDate;

        @JsonProperty("poster_path")
        private String posterPath;

        private int reviewCount;

        public static MovieSummary from(Movie movie, Double averageRating, int reviewCount) {
            return MovieSummary.builder()
                    .id(movie.getId())
                    .title(movie.getTitle())
                    .averageRating(averageRating)
                    .releaseDate(movie.getReleaseDate())
                    .posterPath(movie.getPosterPath() != null ? TMDB_IMAGE_BASE_URL + movie.getPosterPath() : null)
                    .reviewCount(reviewCount)
                    .build();
        }

        public static MovieSummary from(MovieSummary movie, Double averageRating, int reviewCount) {
            return MovieSummary.builder()
                    .id(movie.getId())
                    .title(movie.getTitle())
                    .averageRating(averageRating)
                    .releaseDate(movie.getReleaseDate())
                    .posterPath(movie.getPosterPath() != null ? TMDB_IMAGE_BASE_URL + movie.getPosterPath() : null)
                    .reviewCount(reviewCount)
                    .build();
        }
    }

    public static MovieSearchResponse from(List<MovieSummary> movies) {
        return MovieSearchResponse.builder()
                .movies(movies)
                .build();
    }
}
