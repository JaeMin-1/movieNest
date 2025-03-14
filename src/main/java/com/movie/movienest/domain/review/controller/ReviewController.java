package com.movie.movienest.domain.review.controller;

import com.movie.movienest.domain.review.dto.request.ReviewRequest;
import com.movie.movienest.domain.review.dto.response.ReviewResponse;
import com.movie.movienest.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{movieId}")
    public ResponseEntity<String> createReview(
            @PathVariable Long movieId, @RequestBody ReviewRequest request) {
        reviewService.createReview(movieId, request);
        return ResponseEntity.ok("리뷰 작성 완료");
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Long movieId) {
        return ResponseEntity.ok(reviewService.getReviews(movieId));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(
            @PathVariable Long reviewId, @RequestBody ReviewRequest request) {
        reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok("리뷰 수정 완료");
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok("리뷰 삭제 완료");
    }
}
