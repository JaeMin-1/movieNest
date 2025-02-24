package com.movie.movienest.domain.movie.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class MovieSearchResponse {

    @JsonProperty("results")
    private List<MovieSummary> movies;

    @Getter
    public static class MovieSummary {
        private Long id;
        private String title;
        private Double voteAverage;

        @JsonProperty("release_date")
        private String releaseDate;

        @Setter
        @JsonProperty("poster_path")
        private String posterPath;
    }
}
