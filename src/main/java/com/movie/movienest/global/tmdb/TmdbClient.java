package com.movie.movienest.global.tmdb;

import com.movie.movienest.domain.movie.dto.response.MovieGenreResponse;
import com.movie.movienest.domain.movie.dto.response.MovieSearchResponse;
import com.movie.movienest.domain.movie.entity.Movie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TmdbClient {

    @Value("${tmdb.api-key}")
    private String apiKey;

    @Value("${tmdb.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public MovieSearchResponse searchMovies(String query, int page) {
        String url = baseUrl + "/search/movie?api_key=" + apiKey + "&query=" + query + "&language=ko&page=" + page + "&region=KR";

        return restTemplate.getForObject(url, MovieSearchResponse.class);
    }

    public Movie getMovieDetails(Long movieId) {
        String url = String.format("%s/movie/%d?api_key=%s&language=ko-KR&region=KR&append_to_response=credits", baseUrl, movieId, apiKey);
        return restTemplate.getForObject(url, Movie.class);
    }

    public MovieSearchResponse getMoviesByGenre(Long genreId, int page, String sort) {
        String sortBy = getSortByParam(sort);
        String url = baseUrl + "/discover/movie?api_key=" + apiKey +
                "&with_genres=" + genreId + "&language=ko&page=" + page +
                "&sort_by=" + sortBy + "&region=KR";

        return restTemplate.getForObject(url, MovieSearchResponse.class);
    }

    private String getSortByParam(String sort) {
        return switch (sort) {
            case "date" -> "primary_release_date.desc";  // 최신 개봉일 순
            case "title" -> "original_title.asc";  // 제목 가나다순 정렬
            default -> "popularity.desc";
        };
    }

    public List<MovieSearchResponse.MovieSummary> getMoviesByGenres(List<Integer> genreIds, int page) {
        String genreQuery = genreIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        if (genreQuery.isEmpty()) {
            return Collections.emptyList();
        }

        String url = baseUrl + "/discover/movie?api_key=" + apiKey +
                "&with_genres=" + genreQuery + "&language=ko&page=" + page + "&sort_by=popularity.desc&region=KR";

        MovieSearchResponse response = restTemplate.getForObject(url, MovieSearchResponse.class);

        return response != null ? response.getMovies() : Collections.emptyList();
    }


    public MovieSearchResponse getPopularMovies(int page) {
        String url = baseUrl + "/movie/popular?api_key=" + apiKey + "&language=ko&page=" + page + "&region=KR";

        return restTemplate.getForObject(url, MovieSearchResponse.class);
    }

    public List<Integer> getMovieGenres(Long movieId) {
        String url = String.format("%s/movie/%d?api_key=%s&language=ko-KR&region=KR", baseUrl, movieId, apiKey);
        MovieGenreResponse response = restTemplate.getForObject(url, MovieGenreResponse.class);

        if (response == null || response.getGenres() == null) {
            return Collections.emptyList();
        }

        return response.getGenres().stream()
                .map(MovieGenreResponse.Genre::getId)
                .collect(Collectors.toList());
    }
}
