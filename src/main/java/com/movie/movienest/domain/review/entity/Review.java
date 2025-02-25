package com.movie.movienest.domain.review.entity;

import com.movie.movienest.domain.user.entity.User;
import com.movie.movienest.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long movieId;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private Double rating;

    public void updateReview(String content, Double rating) {
        this.content = content;
        this.rating = rating;
    }
}
