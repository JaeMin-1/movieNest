package com.movie.movienest.domain.movie.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
public class MovieSearchResponse {

    @JsonProperty("results")
    private List<MovieSummary> movies;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("page")
    private int currentPage;

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
    }
}
