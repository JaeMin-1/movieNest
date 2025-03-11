package com.movie.movienest.domain.movie.controller;

import com.movie.movienest.domain.movie.dto.response.MovieDetailResponse;
import com.movie.movienest.domain.movie.dto.response.MovieSearchResponse;
import com.movie.movienest.domain.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/search")
    public ResponseEntity<MovieSearchResponse> searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ResponseEntity.ok(movieService.searchMovies(query, limit, offset));
    }

    @GetMapping("/details/{movieId}")
    public ResponseEntity<MovieDetailResponse> getMovieDetails(
            @PathVariable Long movieId) {
        return ResponseEntity.ok(movieService.getMovieDetails(movieId));
    }
}
