package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.Lot;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Lot entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LotRepository extends R2dbcRepository<Lot, Long>, LotRepositoryInternal {
    Flux<Lot> findAllBy(Pageable pageable);

    @Query("SELECT * FROM lot entity WHERE entity.section_id = :id")
    Flux<Lot> findBySection(Long id);

    @Query("SELECT * FROM lot entity WHERE entity.section_id IS NULL")
    Flux<Lot> findAllWhereSectionIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Lot> findAll();

    @Override
    Mono<Lot> findById(Long id);

    @Override
    <S extends Lot> Mono<S> save(S entity);
}

interface LotRepositoryInternal {
    <S extends Lot> Mono<S> insert(S entity);
    <S extends Lot> Mono<S> save(S entity);
    Mono<Integer> update(Lot entity);

    Flux<Lot> findAll();
    Mono<Lot> findById(Long id);
    Flux<Lot> findAllBy(Pageable pageable);
    Flux<Lot> findAllBy(Pageable pageable, Criteria criteria);
}
