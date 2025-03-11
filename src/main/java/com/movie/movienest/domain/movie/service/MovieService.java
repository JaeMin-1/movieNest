package com.movie.movienest.domain.movie.service;

import com.movie.movienest.domain.favorite.service.FavoriteService;
import com.movie.movienest.domain.movie.dto.response.MovieDetailResponse;
import com.movie.movienest.domain.movie.dto.response.MovieSearchResponse;
import com.movie.movienest.domain.movie.entity.Movie;
import com.movie.movienest.domain.review.dto.response.ReviewResponse;
import com.movie.movienest.domain.review.service.ReviewService;
import com.movie.movienest.domain.user.entity.User;
import com.movie.movienest.domain.user.service.UserService;
import com.movie.movienest.global.tmdb.TmdbClient;
import com.movie.movienest.global.util.MovieSortUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.movie.movienest.global.constants.Constant.*;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final TmdbClient tmdbClient;
    private final ReviewService reviewService;
    private final UserService userService;
    private final FavoriteService favoriteService;

    @Transactional(readOnly = true)
    public MovieSearchResponse searchMovies(String query, int limit, int offset) {
        List<MovieSearchResponse.MovieSummary> movies = IntStream.iterate(1, i -> i + 1)
                .mapToObj(page -> tmdbClient.searchMovies(query, page).getMovies())
                .flatMap(List::stream)
                .skip(offset)
                .limit(limit)
                .map(movie -> MovieSearchResponse.MovieSummary.from(
                        movie,
                        reviewService.getAverageRating(movie.getId()),
                        reviewService.getReviewCount(movie.getId())))
                .collect(Collectors.toList());

        return MovieSearchResponse.from(movies);
    }

    @Transactional(readOnly = true)
    public MovieDetailResponse getMovieDetails(Long movieId) {
        Movie movie = tmdbClient.getMovieDetails(movieId);
        User user = userService.getAuthenticatedUserOrNull();
        boolean isFavorite = favoriteService.isFavorite(user, movieId);
        List<ReviewResponse> reviewResponses = reviewService.getReviewsForMovie(movieId);
        Double averageRating = reviewService.getAverageRating(movieId);

        return MovieDetailResponse.from(movie, averageRating, isFavorite, reviewResponses);
    }

    @Transactional(readOnly = true)
    public MovieSearchResponse getMoviesByGenre(Long genreId, String sort, int limit, int offset) {

        if (RATING.equals(sort) || REVIEW.equals(sort)) {
            return fetchAndSortAllMovies(genreId, sort, limit, offset);
        } else {
            return fetchAndReturnPagedMovies(genreId, sort, limit, offset);
        }
    }

    private MovieSearchResponse fetchAndSortAllMovies(Long genreId, String sort, int limit, int offset) {

        // TMDB에서 최대 500페이지까지 모든 영화 가져오기
        List<MovieSearchResponse.MovieSummary> allMovies = IntStream.rangeClosed(1, MAX_PAGE_FROM_TMDB)
                .mapToObj(i -> tmdbClient.getMoviesByGenre(genreId, i, POPULARITY))
                .flatMap(response -> response.getMovies().stream()
                        .map(movie -> MovieSearchResponse.MovieSummary.from(
                                movie,
                                reviewService.getAverageRating(movie.getId()),
                                reviewService.getReviewCount(movie.getId()))))
                .sorted(MovieSortUtil.getComparator(sort))
                .toList();

        List<MovieSearchResponse.MovieSummary> paginatedMovies = allMovies.stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

        return MovieSearchResponse.from(paginatedMovies);
    }

    private MovieSearchResponse fetchAndReturnPagedMovies(Long genreId, String sort, int limit, int offset) {
        List<MovieSearchResponse.MovieSummary> movies = IntStream.iterate(1, i -> i + 1)
                .mapToObj(page -> tmdbClient.getMoviesByGenre(genreId, page, sort).getMovies())
                .flatMap(List::stream)
                .skip(offset)
                .limit(limit)
                .map(movie -> MovieSearchResponse.MovieSummary.from(
                        movie,
                        reviewService.getAverageRating(movie.getId()),
                        reviewService.getReviewCount(movie.getId())))
                .collect(Collectors.toList());

        return MovieSearchResponse.from(movies);
    }


    @Transactional(readOnly = true)
    public MovieSearchResponse getRecommendedMovies() {
        User user = userService.getAuthenticatedUserOrThrow();

        // 1. 사용자가 좋아한 영화 ID 가져오기
        List<Long> favoriteMovieIds = favoriteService.getUserFavoriteMovieIds(user);
        List<Long> highlyRatedMovieIds = reviewService.getHighlyRatedMovieIdsByUser(user.getId(), 5.0);

        // 2. 영화 목록 합치기
        Set<Long> userPreferredMovieIds = new HashSet<>(favoriteMovieIds);
        userPreferredMovieIds.addAll(highlyRatedMovieIds);

        // 3. 사용자가 좋아한 영화가 없으면 TMDB 인기 영화 제공
        if (userPreferredMovieIds.isEmpty()) {
            return tmdbClient.getPopularMovies(1);
        }

        // 4. 사용자가 선호하는 장르 추출
        Map<Integer, Integer> genreFrequency = userPreferredMovieIds.stream()
                .flatMap(movieId -> tmdbClient.getMovieGenres(movieId).stream())
                .collect(Collectors.toMap(genreId -> genreId, genreId -> 1, Integer::sum));

        // 5. 가중치가 높은 순으로 정렬
        List<Integer> sortedGenres = genreFrequency.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        // 6. 장르 조합을 기반으로 추천 진행
        List<MovieSearchResponse.MovieSummary> recommendedMovies = sortedGenres.stream()
                .flatMap(genreCount -> generateGenreCombinations(sortedGenres, genreCount).stream())
                .flatMap(genreCombo -> tmdbClient.getMoviesByGenres(genreCombo, 1).stream()
                        .filter(movie -> !userPreferredMovieIds.contains(movie.getId())) // 이미 본 영화 제외
                        .map(movie -> MovieSearchResponse.MovieSummary.from(
                                movie,
                                reviewService.getAverageRating(movie.getId()),
                                reviewService.getReviewCount(movie.getId()))))
                .limit(PAGE_SIZE)
                .collect(Collectors.toList());

        // 7. 추천 영화가 부족하면 인기 영화 추가
        if (recommendedMovies.size() < PAGE_SIZE) {
            List<MovieSearchResponse.MovieSummary> popularMovies = tmdbClient.getPopularMovies(1).getMovies();
            int moviesToAdd = PAGE_SIZE - recommendedMovies.size();
            recommendedMovies.addAll(popularMovies.subList(0, Math.min(moviesToAdd, popularMovies.size())));
        }

        return MovieSearchResponse.from(recommendedMovies);
    }

    // 장르 조합 생성 메서드
    private List<List<Integer>> generateGenreCombinations(List<Integer> genres, int size) {
        List<List<Integer>> result = new ArrayList<>();
        generateCombinationsHelper(genres, size, 0, new ArrayList<>(), result);
        return result;
    }

    // 재귀 헬퍼 메서드
    private void generateCombinationsHelper(List<Integer> genres, int size, int start, List<Integer> temp, List<List<Integer>> result) {
        if (temp.size() == size) {
            result.add(new ArrayList<>(temp));
            return;
        }

        for (int i = start; i < genres.size(); i++) {
            temp.add(genres.get(i));
            generateCombinationsHelper(genres, size, i + 1, temp, result);
            temp.remove(temp.size() - 1);
        }
    }
}
