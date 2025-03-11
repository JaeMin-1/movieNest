package com.movie.movienest.domain.movie.dto.response;

import com.movie.movienest.domain.movie.entity.Movie;
import com.movie.movienest.domain.review.dto.response.ReviewResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MovieDetailResponse {
    private final Long id;
    private final String title;
    private final String overview;
    private final Double averageRating;
    private final String releaseDate;
    private final String posterPath;
    private final Integer runtime;
    private final List<Genre> genres;
    private final String director;
    private final List<Actor> mainActors;
    private final List<ReviewResponse> userReviews;
    private boolean isFavorite;

    @Getter
    @Builder
    public static class Genre {
        private final String name;
    }

    @Getter
    @Builder
    public static class Actor {
        private final String name;
        private final String character;
    }

    public static MovieDetailResponse from(Movie movie, Double averageRating, boolean isFavorite, List<ReviewResponse> reviewResponses) {
        return MovieDetailResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .overview(movie.getOverview())
                .averageRating(averageRating)
                .releaseDate(movie.getReleaseDate())
                .posterPath(movie.getPosterPath() != null ? "https://image.tmdb.org/t/p/w500" + movie.getPosterPath() : null)
                .runtime(movie.getRuntime())
                .genres(movie.getGenres().stream()
                        .map(g -> Genre.builder().name(g.getName()).build())
                        .collect(Collectors.toList()))
                .director(movie.getCredits().getCrew().stream()
                        .filter(c -> "Director".equals(c.getJob()))
                        .map(Movie.CrewMember::getName)
                        .findFirst()
                        .orElse("정보 없음"))
                .mainActors(movie.getCredits().getCast().stream()
                        .limit(5)
                        .map(actor -> Actor.builder()
                                .name(actor.getName())
                                .character(actor.getCharacter())
                                .build())
                        .collect(Collectors.toList()))
                .userReviews(reviewResponses)
                .isFavorite(isFavorite)
                .build();
    }
}
