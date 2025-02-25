package com.movie.movienest.domain.movie.controller;

import com.movie.movienest.domain.movie.dto.response.MovieDetailResponse;
import com.movie.movienest.domain.movie.dto.response.MovieSearchResponse;
import com.movie.movienest.domain.movie.service.MovieService;
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
public class MovieController {

    private final MovieService movieService;
    private final UserRepository userRepository;

    @GetMapping("/search")
    public ResponseEntity<MovieSearchResponse> searchMovies(@RequestParam String query) {
        return ResponseEntity.ok(movieService.searchMovies(query));
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDetailResponse> getMovieDetails(
            @PathVariable Long movieId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = (userDetails != null) ? userRepository.findByEmail(userDetails.getUsername()).orElse(null) : null;

        return ResponseEntity.ok(movieService.getMovieDetails(movieId, user));
    }
}
