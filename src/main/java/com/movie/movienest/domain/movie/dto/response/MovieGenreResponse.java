package com.movie.movienest.domain.movie.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class MovieGenreResponse {

    private List<Genre> genres;

    @Getter
    public static class Genre {
        private int id;
    }
}
