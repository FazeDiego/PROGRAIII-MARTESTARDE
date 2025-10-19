package com.rutear.demo.repository;

import reactor.core.publisher.Mono;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import com.rutear.demo.model.MovieEntity;


public interface MovieRepository extends ReactiveNeo4jRepository<MovieEntity, String> {
    Mono<MovieEntity> findByTitle(String title);
}