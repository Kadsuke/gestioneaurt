package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.ModeEvacExcreta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the ModeEvacExcreta entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ModeEvacExcretaRepository extends R2dbcRepository<ModeEvacExcreta, Long>, ModeEvacExcretaRepositoryInternal {
    Flux<ModeEvacExcreta> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<ModeEvacExcreta> findAll();

    @Override
    Mono<ModeEvacExcreta> findById(Long id);

    @Override
    <S extends ModeEvacExcreta> Mono<S> save(S entity);
}

interface ModeEvacExcretaRepositoryInternal {
    <S extends ModeEvacExcreta> Mono<S> insert(S entity);
    <S extends ModeEvacExcreta> Mono<S> save(S entity);
    Mono<Integer> update(ModeEvacExcreta entity);

    Flux<ModeEvacExcreta> findAll();
    Mono<ModeEvacExcreta> findById(Long id);
    Flux<ModeEvacExcreta> findAllBy(Pageable pageable);
    Flux<ModeEvacExcreta> findAllBy(Pageable pageable, Criteria criteria);
}
