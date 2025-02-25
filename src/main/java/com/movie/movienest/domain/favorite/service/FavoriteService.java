package com.movie.movienest.domain.favorite.service;

import com.movie.movienest.domain.favorite.entity.Favorite;
import com.movie.movienest.domain.favorite.repository.FavoriteRepository;
import com.movie.movienest.domain.movie.dto.response.MovieSearchResponse;
import com.movie.movienest.domain.review.repository.ReviewRepository;
import com.movie.movienest.domain.user.entity.User;
import com.movie.movienest.global.tmdb.TmdbClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;
    private final TmdbClient tmdbClient;

    @Transactional
    public String toggleFavorite(User user, Long movieId) {
        var favoriteOpt = favoriteRepository.findByUserAndMovieId(user, movieId);

        if (favoriteOpt.isPresent()) {
            favoriteRepository.delete(favoriteOpt.get());
            return "즐겨찾기 삭제 완료";
        } else {
            favoriteRepository.save(Favorite.builder().user(user).movieId(movieId).build());
            return "즐겨찾기 추가 완료";
        }
    }

    @Transactional(readOnly = true)
    public List<MovieSearchResponse.MovieSummary> getFavorites(User user, String sort) {
        List<Favorite> favorites = favoriteRepository.findByUser(user);

        return favorites.stream()
                .map(favorite -> {
                    var movie = tmdbClient.getMovieDetails(favorite.getMovieId());
                    Double averageRating = reviewRepository.findAverageRatingByMovieId(movie.getId()).orElse(0.0);
                    int reviewCount = reviewRepository.countByMovieId(movie.getId());

                    return MovieSearchResponse.MovieSummary.builder()
                            .id(movie.getId())
                            .title(movie.getTitle())
                            .averageRating(averageRating)
                            .releaseDate(movie.getReleaseDate())
                            .posterPath(movie.getPosterPath() != null ? "https://image.tmdb.org/t/p/w500" + movie.getPosterPath() : null)
                            .reviewCount(reviewCount)
                            .build();
                })
                .sorted(getComparator(sort))
                .collect(Collectors.toList());
    }

    private Comparator<MovieSearchResponse.MovieSummary> getComparator(String sort) {
        return switch (sort) {
            case "rating" -> Comparator.comparing(MovieSearchResponse.MovieSummary::getAverageRating).reversed();
            case "review" -> Comparator.comparing(MovieSearchResponse.MovieSummary::getReviewCount).reversed();
            case "date" -> Comparator.comparing(MovieSearchResponse.MovieSummary::getReleaseDate).reversed();
            default -> Comparator.comparing(MovieSearchResponse.MovieSummary::getTitle);
        };
    }
}
