package com.movie.movienest.domain.movie.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
public class MovieSearchResponse {

    @JsonProperty("results")
    private List<MovieSummary> movies;

    @Getter
    @Builder
    public static class MovieSummary {
        private Long id;
        private String title;
        private Double averageRating;

        @JsonProperty("release_date")
        private String releaseDate;

        @Setter
        @JsonProperty("poster_path")
        private String posterPath;

        private int reviewCount;
    }
}
