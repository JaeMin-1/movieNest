package com.movie.movienest.domain.movie.controller;

import com.movie.movienest.domain.movie.dto.response.MovieSearchResponse;
import com.movie.movienest.domain.movie.service.MovieService;
import com.movie.movienest.domain.user.entity.User;
import com.movie.movienest.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class RecommendController {

    private final MovieService movieService;
    private final UserRepository userRepository;

    @GetMapping("/recommend")
    public ResponseEntity<MovieSearchResponse> getRecommendedMovies(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return ResponseEntity.ok(movieService.getRecommendedMovies(user));
    }
}

