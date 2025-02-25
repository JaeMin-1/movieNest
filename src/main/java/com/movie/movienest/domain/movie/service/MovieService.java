package com.movie.movienest.domain.movie.service;

import com.movie.movienest.domain.movie.dto.response.MovieDetailResponse;
import com.movie.movienest.domain.movie.dto.response.MovieSearchResponse;
import com.movie.movienest.domain.movie.entity.Movie;
import com.movie.movienest.domain.review.dto.response.ReviewResponse;
import com.movie.movienest.domain.review.repository.ReviewRepository;
import com.movie.movienest.global.tmdb.TmdbClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final TmdbClient tmdbClient;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public MovieSearchResponse searchMovies(String query) {
        MovieSearchResponse response = tmdbClient.searchMovies(query);

        List<MovieSearchResponse.MovieSummary> movies = response.getMovies().stream()
                .map(movie -> MovieSearchResponse.MovieSummary.builder()
                        .id(movie.getId())
                        .title(movie.getTitle())
                        .averageRating(reviewRepository.findAverageRatingByMovieId(movie.getId()).orElse(0.0)) // ✅ 유저 평점 평균 추가
                        .releaseDate(movie.getReleaseDate())
                        .posterPath(movie.getPosterPath() != null ? "https://image.tmdb.org/t/p/w500" + movie.getPosterPath() : null)
                        .build()
                )
                .collect(Collectors.toList());

        return MovieSearchResponse.builder()
                .movies(movies)
                .build();
    }

    @Transactional(readOnly = true)
    public MovieDetailResponse getMovieDetails(Long movieId) {
        Movie movie = tmdbClient.getMovieDetails(movieId);

        // 리뷰 목록을 ReviewResponse로 변환
        List<ReviewResponse> reviewResponses = reviewRepository.findByMovieId(movieId)
                .stream()
                .map(ReviewResponse::fromEntity) // ✅ Review -> ReviewResponse 변환
                .collect(Collectors.toList());

        return MovieDetailResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .overview(movie.getOverview())
                .averageRating(reviewRepository.findAverageRatingByMovieId(movieId).orElse(0.0))
                .releaseDate(movie.getReleaseDate())
                .posterPath(movie.getPosterPath() != null ? "https://image.tmdb.org/t/p/w500" + movie.getPosterPath() : null)
                .runtime(movie.getRuntime())
                .genres(movie.getGenres().stream()
                        .map(g -> MovieDetailResponse.Genre.builder().name(g.getName()).build())
                        .collect(Collectors.toList()))
                .director(movie.getCredits().getCrew().stream()
                        .filter(c -> "Director".equals(c.getJob()))
                        .map(Movie.CrewMember::getName)
                        .findFirst()
                        .orElse("정보 없음"))
                .mainActors(movie.getCredits().getCast().stream()
                        .limit(5)
                        .map(actor -> MovieDetailResponse.Actor.builder()
                                .name(actor.getName())
                                .character(actor.getCharacter())
                                .build())
                        .collect(Collectors.toList()))
                .userReviews(reviewResponses)
                .build();
    }
}
