package com.movie.movienest.domain.review.service;

import com.movie.movienest.domain.review.dto.request.ReviewRequest;
import com.movie.movienest.domain.review.dto.response.ReviewResponse;
import com.movie.movienest.domain.review.entity.Review;
import com.movie.movienest.domain.review.repository.ReviewRepository;
import com.movie.movienest.domain.user.entity.User;
import com.movie.movienest.domain.user.service.UserService;
import com.movie.movienest.global.exception.CustomException;
import com.movie.movienest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;

    @Transactional
    public void createReview(Long movieId, ReviewRequest request) {
        User user = userService.getAuthenticatedUserOrThrow();

        if (reviewRepository.findByMovieIdAndUserId(movieId, user.getId()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_REVIEW);
        }

        Review review = Review.builder()
                .user(user)
                .movieId(movieId)
                .rating(request.getRating())
                .content(request.getContent())
                .build();

        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviews(Long movieId) {
        return reviewRepository.findByMovieId(movieId).stream()
                .map(ReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateReview(Long reviewId, ReviewRequest request) {
        User user = userService.getAuthenticatedUserOrThrow();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        review.updateReview(request.getContent(), request.getRating());
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        User user = userService.getAuthenticatedUserOrThrow();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(Long movieId) {
        return reviewRepository.findAverageRatingByMovieId(movieId).orElse(0.0);
    }

    @Transactional(readOnly = true)
    public int getReviewCount(Long movieId) {
        return reviewRepository.countByMovieId(movieId);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsForMovie(Long movieId) {
        return reviewRepository.findByMovieId(movieId)
                .stream()
                .map(ReviewResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Long> getHighlyRatedMovieIdsByUser(Long userId, double ratingThreshold) {
        return reviewRepository.findHighlyRatedMovieIdsByUser(userId, ratingThreshold);
    }
}
