package com.movie.movienest.domain.movie.service;

import com.movie.movienest.domain.favorite.repository.FavoriteRepository;
import com.movie.movienest.domain.movie.dto.response.MovieDetailResponse;
import com.movie.movienest.domain.movie.dto.response.MovieSearchResponse;
import com.movie.movienest.domain.movie.entity.Movie;
import com.movie.movienest.domain.review.dto.response.ReviewResponse;
import com.movie.movienest.domain.review.repository.ReviewRepository;
import com.movie.movienest.domain.user.entity.User;
import com.movie.movienest.global.tmdb.TmdbClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final TmdbClient tmdbClient;
    private final ReviewRepository reviewRepository;
    private final FavoriteRepository favoriteRepository;

    @Transactional(readOnly = true)
    public MovieSearchResponse searchMovies(String query, int page) {
        var response = tmdbClient.searchMovies(query, page);

        List<MovieSearchResponse.MovieSummary> movies = response.getMovies().stream()
                .map(movie -> MovieSearchResponse.MovieSummary.builder()
                        .id(movie.getId())
                        .title(movie.getTitle())
                        .averageRating(reviewRepository.findAverageRatingByMovieId(movie.getId()).orElse(0.0))
                        .releaseDate(movie.getReleaseDate())
                        .posterPath(movie.getPosterPath() != null ? "https://image.tmdb.org/t/p/w500" + movie.getPosterPath() : null)
                        .reviewCount(reviewRepository.countByMovieId(movie.getId()))
                        .build()
                )
                .collect(Collectors.toList());

        return MovieSearchResponse.builder()
                .movies(movies)
                .totalPages(response.getTotalPages())
                .currentPage(page)
                .build();
    }

    @Transactional(readOnly = true)
    public MovieDetailResponse getMovieDetails(Long movieId, User user) {
        Movie movie = tmdbClient.getMovieDetails(movieId);

        boolean isFavorite = user != null && favoriteRepository.findByUserAndMovieId(user, movieId).isPresent();

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
                .isFavorite(isFavorite)
                .build();
    }

    @Transactional(readOnly = true)
    public MovieSearchResponse getMoviesByGenre(Long genreId, String sort, int page) {
        if (sort.equals("rating") || sort.equals("review")) {
            return fetchAndSortAllMovies(genreId, sort, page);
        } else {
            return fetchAndReturnPagedMovies(genreId, sort, page);
        }
    }

    private MovieSearchResponse fetchAndSortAllMovies(Long genreId, String sort, int page) {
        int maxPagesToFetch = 500; // ✅ TMDB 최대 페이지 제한 (모든 영화 가져오기)

        List<MovieSearchResponse.MovieSummary> allMovies = new ArrayList<>();

        // ✅ TMDB에서 모든 페이지 가져오기
        for (int i = 1; i <= maxPagesToFetch; i++) {
            var response = tmdbClient.getMoviesByGenre(genreId, i, "popularity");
            List<MovieSearchResponse.MovieSummary> movies = response.getMovies().stream()
                    .map(movie -> MovieSearchResponse.MovieSummary.builder()
                            .id(movie.getId())
                            .title(movie.getTitle())
                            .averageRating(reviewRepository.findAverageRatingByMovieId(movie.getId()).orElse(0.0))
                            .releaseDate(movie.getReleaseDate())
                            .posterPath(movie.getPosterPath() != null ? "https://image.tmdb.org/t/p/w500" + movie.getPosterPath() : null)
                            .reviewCount(reviewRepository.countByMovieId(movie.getId()))
                            .build()
                    )
                    .toList();
            allMovies.addAll(movies);

            if (i >= response.getTotalPages()) {
                break;
            }
        }

        // 평점 또는 리뷰 개수 기준으로 전체 정렬
        allMovies.sort(getCustomComparator(sort));

        // 요청한 페이지의 데이터만 반환 (한 페이지당 20개)
        int startIndex = (page - 1) * 20;
        int endIndex = Math.min(startIndex + 20, allMovies.size());
        List<MovieSearchResponse.MovieSummary> paginatedMovies = allMovies.subList(startIndex, endIndex);

        return MovieSearchResponse.builder()
                .movies(paginatedMovies)
                .totalPages((int) Math.ceil((double) allMovies.size() / 20)) // ✅ 전체 정렬된 데이터를 기준으로 총 페이지 수 계산
                .currentPage(page)
                .build();
    }

    private MovieSearchResponse fetchAndReturnPagedMovies(Long genreId, String sort, int page) {
        var response = tmdbClient.getMoviesByGenre(genreId, page, sort);

        List<MovieSearchResponse.MovieSummary> movies = response.getMovies().stream()
                .map(movie -> MovieSearchResponse.MovieSummary.builder()
                        .id(movie.getId())
                        .title(movie.getTitle())
                        .averageRating(reviewRepository.findAverageRatingByMovieId(movie.getId()).orElse(0.0))
                        .releaseDate(movie.getReleaseDate())
                        .posterPath(movie.getPosterPath() != null ? "https://image.tmdb.org/t/p/w500" + movie.getPosterPath() : null)
                        .reviewCount(reviewRepository.countByMovieId(movie.getId()))
                        .build()
                )
                .collect(Collectors.toList());

        return MovieSearchResponse.builder()
                .movies(movies)
                .totalPages(response.getTotalPages())
                .currentPage(page)
                .build();
    }

    private Comparator<MovieSearchResponse.MovieSummary> getCustomComparator(String sort) {
        return switch (sort) {
            case "rating" -> Comparator.comparing(MovieSearchResponse.MovieSummary::getAverageRating).reversed();
            case "review" -> Comparator.comparing(MovieSearchResponse.MovieSummary::getReviewCount).reversed();
            default -> Comparator.comparing(MovieSearchResponse.MovieSummary::getReleaseDate).reversed();
        };
    }
}
