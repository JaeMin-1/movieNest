package com.movie.movienest.domain.review.repository;

import com.movie.movienest.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByMovieId(Long movieId);
    Optional<Review> findByMovieIdAndUserId(Long movieId, Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.movieId = :movieId")
    Optional<Double> findAverageRatingByMovieId(Long movieId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.movieId = :movieId")
    int countByMovieId(Long movieId);
}

