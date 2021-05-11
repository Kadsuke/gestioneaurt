package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.Parcelle;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Parcelle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParcelleRepository extends R2dbcRepository<Parcelle, Long>, ParcelleRepositoryInternal {
    Flux<Parcelle> findAllBy(Pageable pageable);

    @Query("SELECT * FROM parcelle entity WHERE entity.lot_id = :id")
    Flux<Parcelle> findByLot(Long id);

    @Query("SELECT * FROM parcelle entity WHERE entity.lot_id IS NULL")
    Flux<Parcelle> findAllWhereLotIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Parcelle> findAll();

    @Override
    Mono<Parcelle> findById(Long id);

    @Override
    <S extends Parcelle> Mono<S> save(S entity);
}

interface ParcelleRepositoryInternal {
    <S extends Parcelle> Mono<S> insert(S entity);
    <S extends Parcelle> Mono<S> save(S entity);
    Mono<Integer> update(Parcelle entity);

    Flux<Parcelle> findAll();
    Mono<Parcelle> findById(Long id);
    Flux<Parcelle> findAllBy(Pageable pageable);
    Flux<Parcelle> findAllBy(Pageable pageable, Criteria criteria);
}
