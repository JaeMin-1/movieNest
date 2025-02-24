package com.movie.movienest.domain.movie.service;

import com.movie.movienest.domain.movie.dto.response.MovieDetailResponse;
import com.movie.movienest.domain.movie.dto.response.MovieSearchResponse;
import com.movie.movienest.domain.movie.entity.Movie;
import com.movie.movienest.domain.review.entity.Review;
import com.movie.movienest.domain.review.repository.ReviewRepository;
import com.movie.movienest.global.tmdb.TmdbClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final TmdbClient tmdbClient;
    private final ReviewRepository reviewRepository;

    public List<MovieSearchResponse.MovieSummary> searchMovies(String query) {
        MovieSearchResponse response = tmdbClient.searchMovies(query);

        response.getMovies().forEach(movie -> {
            if (movie.getPosterPath() != null) {
                movie.setPosterPath("https://image.tmdb.org/t/p/w500" + movie.getPosterPath());
            }
        });

        return response.getMovies();
    }

    @Transactional(readOnly = true)
    public MovieDetailResponse getMovieDetails(Long movieId) {
        Movie movieData = tmdbClient.getMovieDetails(movieId);

        // 영화의 리뷰 정보 가져오기
        List<Review> reviews = reviewRepository.findByMovieId(movieId);

        // Entity → DTO 변환하여 반환
        return movieData.toDto(reviews);
    }
}
