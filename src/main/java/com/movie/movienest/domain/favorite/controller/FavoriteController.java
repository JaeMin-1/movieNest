package com.movie.movienest.domain.favorite.controller;

import com.movie.movienest.domain.favorite.service.FavoriteService;
import com.movie.movienest.domain.movie.dto.response.MovieSearchResponse;
import com.movie.movienest.domain.user.entity.User;
import com.movie.movienest.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserRepository userRepository;

    @PostMapping("/{movieId}/favorite")
    public ResponseEntity<String> toggleFavorite(
            @PathVariable Long movieId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return ResponseEntity.ok(favoriteService.toggleFavorite(user, movieId));
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<MovieSearchResponse.MovieSummary>> getFavorites(
            @RequestParam(defaultValue = "title") String sort,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return ResponseEntity.ok(favoriteService.getFavorites(user, sort));
    }
}
