package com.movie.movienest.domain.review.dto.response;

import com.movie.movienest.domain.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponse {
    private Long reviewId;
    private String reviewer;
    private String content;
    private Double rating;
    private LocalDateTime timestamp;

    public static ReviewResponse fromEntity(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getId())
                .reviewer(review.getUser().getNickname())
                .content(review.getContent())
                .rating(review.getRating())
                .timestamp(review.getUpdatedAt().isAfter(review.getCreatedAt()) ? review.getUpdatedAt() : review.getCreatedAt())
                .build();
    }
}
