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

    @JsonProperty("total_pages") // TMDB 응답의 total_pages와 매핑
    private int totalPages;

    @JsonProperty("page") // 현재 페이지 번호 추가
    private int currentPage;

    @Getter
    @Setter
    @Builder
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
