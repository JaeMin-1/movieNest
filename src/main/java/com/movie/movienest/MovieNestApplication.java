package com.movie.movienest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MovieNestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieNestApplication.class, args);
    }

}
