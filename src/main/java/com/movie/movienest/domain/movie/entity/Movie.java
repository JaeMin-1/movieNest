package com.movie.movienest.domain.movie.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.movie.movienest.domain.movie.dto.response.MovieDetailResponse;
import com.movie.movienest.domain.review.dto.response.ReviewResponse;
import com.movie.movienest.domain.review.entity.Review;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Movie {
    private Long id;
    private String title;
    private String overview;
    private Double averageRating;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("poster_path")
    private String posterPath;

    private Integer runtime;
    private List<Genre> genres;
    private Credits credits;

    @Getter
    public static class Genre {
        private String name;
    }

    @Getter
    public static class Credits {
        private List<CrewMember> crew;
        private List<Actor> cast;
    }

    @Getter
    public static class CrewMember {
        private String name;
        private String job;
    }

    @Getter
    public static class Actor {
        private String name;
        private String character;
    }
}
