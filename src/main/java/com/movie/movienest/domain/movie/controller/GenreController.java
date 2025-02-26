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

    @GetMapping("/genre/{genreId}")
    public ResponseEntity<MovieSearchResponse> getMoviesByGenre(
            @PathVariable(required = false) Long genreId,
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "1") int page) {

        Long finalGenreId = (genreId != null) ? genreId : 28L;
        return ResponseEntity.ok(movieService.getMoviesByGenre(finalGenreId, sort, page));
    }
}
