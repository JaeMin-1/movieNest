package com.movie.movienest.domain.movie.controller;

import com.movie.movienest.domain.movie.dto.response.MovieSearchResponse;
import com.movie.movienest.domain.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class GenreController {

    private final MovieService movieService;

    @GetMapping("/genre")
    public ResponseEntity<MovieSearchResponse> getDefaultGenreMovies(
            @RequestParam(defaultValue = "popularity") String sort,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {

        return ResponseEntity.ok(movieService.getMoviesByGenre(28L, sort, limit, offset));
    }

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<MovieSearchResponse> getMoviesByGenre(
            @PathVariable Long genreId,
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(movieService.getMoviesByGenre(genreId, sort, limit, offset));
    }
}
