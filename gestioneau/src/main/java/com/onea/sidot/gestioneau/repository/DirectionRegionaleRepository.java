package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.DirectionRegionale;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the DirectionRegionale entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DirectionRegionaleRepository extends R2dbcRepository<DirectionRegionale, Long>, DirectionRegionaleRepositoryInternal {
    Flux<DirectionRegionale> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<DirectionRegionale> findAll();

    @Override
    Mono<DirectionRegionale> findById(Long id);

    @Override
    <S extends DirectionRegionale> Mono<S> save(S entity);
}

interface DirectionRegionaleRepositoryInternal {
    <S extends DirectionRegionale> Mono<S> insert(S entity);
    <S extends DirectionRegionale> Mono<S> save(S entity);
    Mono<Integer> update(DirectionRegionale entity);

    Flux<DirectionRegionale> findAll();
    Mono<DirectionRegionale> findById(Long id);
    Flux<DirectionRegionale> findAllBy(Pageable pageable);
    Flux<DirectionRegionale> findAllBy(Pageable pageable, Criteria criteria);
}
