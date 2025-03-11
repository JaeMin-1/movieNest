package com.movie.movienest.domain.favorite.controller;

import com.movie.movienest.domain.favorite.service.FavoriteService;
import com.movie.movienest.domain.movie.dto.response.MovieSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{movieId}")
    public ResponseEntity<String> toggleFavorite(@PathVariable Long movieId) {
        return ResponseEntity.ok(favoriteService.toggleFavorite(movieId));
    }

    @GetMapping
    public ResponseEntity<List<MovieSearchResponse.MovieSummary>> getFavorites(
            @RequestParam(defaultValue = "date") String sort) {
        return ResponseEntity.ok(favoriteService.getFavorites(sort));
    }
}
