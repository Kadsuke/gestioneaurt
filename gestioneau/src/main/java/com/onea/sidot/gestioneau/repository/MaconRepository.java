package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.Macon;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Macon entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MaconRepository extends R2dbcRepository<Macon, Long>, MaconRepositoryInternal {
    Flux<Macon> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<Macon> findAll();

    @Override
    Mono<Macon> findById(Long id);

    @Override
    <S extends Macon> Mono<S> save(S entity);
}

interface MaconRepositoryInternal {
    <S extends Macon> Mono<S> insert(S entity);
    <S extends Macon> Mono<S> save(S entity);
    Mono<Integer> update(Macon entity);

    Flux<Macon> findAll();
    Mono<Macon> findById(Long id);
    Flux<Macon> findAllBy(Pageable pageable);
    Flux<Macon> findAllBy(Pageable pageable, Criteria criteria);
}
