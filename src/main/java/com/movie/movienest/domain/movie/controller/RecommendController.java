package com.movie.movienest.domain.movie.controller;

import com.movie.movienest.domain.movie.dto.response.MovieSearchResponse;
import com.movie.movienest.domain.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class RecommendController {

    private final MovieService movieService;

    @GetMapping("/recommend")
    public ResponseEntity<MovieSearchResponse> getRecommendedMovies() {
        return ResponseEntity.ok(movieService.getRecommendedMovies());
    }
}

