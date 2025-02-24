package com.movie.movienest.domain.movie.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.movie.movienest.domain.movie.dto.response.MovieDetailResponse;
import com.movie.movienest.domain.review.entity.Review;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Movie {
    private Long id;
    private String title;
    private String overview;
    private Double voteAverage;

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

    public MovieDetailResponse toDto(List<Review> userReviews) {
        return MovieDetailResponse.builder()
                .id(this.id)
                .title(this.title)
                .overview(this.overview)
                .voteAverage(this.voteAverage)
                .releaseDate(this.releaseDate)
                .posterPath("https://image.tmdb.org/t/p/w500" + this.posterPath)
                .runtime(this.runtime)
                .genres(this.genres.stream()
                        .map(g -> MovieDetailResponse.Genre.builder().name(g.getName()).build())
                        .collect(Collectors.toList()))
                .director(this.credits.getCrew().stream()
                        .filter(c -> "Director".equals(c.getJob()))
                        .map(CrewMember::getName)
                        .findFirst()
                        .orElse("정보 없음"))
                .mainActors(this.credits.getCast().stream()
                        .limit(5)
                        .map(actor -> MovieDetailResponse.Actor.builder()
                                .name(actor.getName())
                                .character(actor.getCharacter())
                                .build())
                        .collect(Collectors.toList()))
                .userReviews(userReviews)
                .build();
    }
}
