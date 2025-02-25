package com.movie.movienest.domain.favorite.repository;

import com.movie.movienest.domain.favorite.entity.Favorite;
import com.movie.movienest.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserAndMovieId(User user, Long movieId);
    List<Favorite> findByUser(User user);
}
