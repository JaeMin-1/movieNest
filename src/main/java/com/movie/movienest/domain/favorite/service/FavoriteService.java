package com.movie.movienest.domain.favorite.service;

import com.movie.movienest.domain.favorite.entity.Favorite;
import com.movie.movienest.domain.favorite.repository.FavoriteRepository;
import com.movie.movienest.domain.movie.dto.response.MovieSearchResponse;
import com.movie.movienest.domain.movie.entity.Movie;
import com.movie.movienest.domain.review.service.ReviewService;
import com.movie.movienest.domain.user.entity.User;
import com.movie.movienest.domain.user.service.UserService;
import com.movie.movienest.global.tmdb.TmdbClient;
import com.movie.movienest.global.util.MovieSortUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ReviewService reviewService;
    private final UserService userService;
    private final TmdbClient tmdbClient;

    @Transactional
    public String toggleFavorite(Long movieId) {
        User user = userService.getAuthenticatedUserOrThrow();

        return favoriteRepository.findByUserAndMovieId(user, movieId)
                .map(favorite -> {
                    favoriteRepository.delete(favorite);
                    return "즐겨찾기 삭제 완료";
                })
                .orElseGet(() -> {
                    favoriteRepository.save(Favorite.builder().user(user).movieId(movieId).build());
                    return "즐겨찾기 추가 완료";
                });
    }

    @Transactional(readOnly = true)
    public List<MovieSearchResponse.MovieSummary> getFavorites(String sort) {
        User user = userService.getAuthenticatedUserOrThrow();
        List<Favorite> favorites = favoriteRepository.findByUser(user);

        return favorites.stream()
                .map(favorite -> {
                    Movie movie = tmdbClient.getMovieDetails(favorite.getMovieId());
                    return MovieSearchResponse.MovieSummary.from(
                            movie,
                            reviewService.getAverageRating(movie.getId()),
                            reviewService.getReviewCount(movie.getId()));
                })
                .sorted(MovieSortUtil.getComparator(sort))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(User user, Long movieId) {
        if (user == null) return false; // 인증되지 않은 사용자는 즐겨찾기할 수 없음
        return favoriteRepository.findByUserAndMovieId(user, movieId).isPresent();
    }

    @Transactional(readOnly = true)
    public List<Long> getUserFavoriteMovieIds(User user) {
        return favoriteRepository.findMovieIdsByUser(user);
    }
}
