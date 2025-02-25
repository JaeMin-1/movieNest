package com.movie.movienest.domain.movie.dto.response;

import com.movie.movienest.domain.movie.entity.Movie;
import com.movie.movienest.domain.review.dto.response.ReviewResponse;
import com.movie.movienest.domain.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

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
}
