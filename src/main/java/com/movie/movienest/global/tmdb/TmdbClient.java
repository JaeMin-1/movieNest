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

    public MovieSearchResponse searchMovies(String query) {
        String url = String.format("%s/search/movie?api_key=%s&query=%s&language=ko-KR", baseUrl, apiKey, query);
        return restTemplate.getForObject(url, MovieSearchResponse.class);
    }

    public Movie getMovieDetails(Long movieId) {
        String url = String.format("%s/movie/%d?api_key=%s&language=ko-KR&append_to_response=credits", baseUrl, movieId, apiKey);
        return restTemplate.getForObject(url, Movie.class);
    }
}
