package com.movie.movienest.domain.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReviewRequest {

    @Min(1)
    @Max(10)
    private double rating; // 1~10점 사이의 평점

    @NotBlank
    private String content; // 리뷰 내용 (공백 불가)
}
