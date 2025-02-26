package com.movie.movienest.global.tmdb;

import com.movie.movienest.domain.movie.dto.response.MovieSearchResponse;
import com.movie.movienest.domain.movie.entity.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class TmdbClient {

    @Value("${tmdb.api-key}")
    private String apiKey;

    @Value("${tmdb.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public MovieSearchResponse searchMovies(String query, int page) {
        String url = baseUrl + "/search/movie?api_key=" + apiKey + "&query=" + query + "&language=ko&page=" + page;

        return restTemplate.getForObject(url, MovieSearchResponse.class);
    }

    public Movie getMovieDetails(Long movieId) {
        String url = String.format("%s/movie/%d?api_key=%s&language=ko-KR&append_to_response=credits", baseUrl, movieId, apiKey);
        return restTemplate.getForObject(url, Movie.class);
    }

    public MovieSearchResponse getMoviesByGenre(Long genreId, int page, String sort) {
        String sortBy = getSortByParam(sort);
        String url = baseUrl + "/discover/movie?api_key=" + apiKey +
                "&with_genres=" + genreId + "&language=ko&page=" + page +
                "&sort_by=" + sortBy;

        return restTemplate.getForObject(url, MovieSearchResponse.class);
    }

    private String getSortByParam(String sort) {
        return switch (sort) {
            case "date" -> "primary_release_date.desc";  // 최신 개봉일 순
            case "title" -> "original_title.asc";  // 제목 가나다순 정렬
            default -> "popularity.desc";
        };
    }
}
