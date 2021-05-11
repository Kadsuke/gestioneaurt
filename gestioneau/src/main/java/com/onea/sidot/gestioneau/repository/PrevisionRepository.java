package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.Prevision;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Prevision entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PrevisionRepository extends R2dbcRepository<Prevision, Long>, PrevisionRepositoryInternal {
    Flux<Prevision> findAllBy(Pageable pageable);

    @Query("SELECT * FROM prevision entity WHERE entity.centre_id = :id")
    Flux<Prevision> findByCentre(Long id);

    @Query("SELECT * FROM prevision entity WHERE entity.centre_id IS NULL")
    Flux<Prevision> findAllWhereCentreIsNull();

    @Query("SELECT * FROM prevision entity WHERE entity.refannee_id = :id")
    Flux<Prevision> findByRefannee(Long id);

    @Query("SELECT * FROM prevision entity WHERE entity.refannee_id IS NULL")
    Flux<Prevision> findAllWhereRefanneeIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Prevision> findAll();

    @Override
    Mono<Prevision> findById(Long id);

    @Override
    <S extends Prevision> Mono<S> save(S entity);
}

interface PrevisionRepositoryInternal {
    <S extends Prevision> Mono<S> insert(S entity);
    <S extends Prevision> Mono<S> save(S entity);
    Mono<Integer> update(Prevision entity);

    Flux<Prevision> findAll();
    Mono<Prevision> findById(Long id);
    Flux<Prevision> findAllBy(Pageable pageable);
    Flux<Prevision> findAllBy(Pageable pageable, Criteria criteria);
}
