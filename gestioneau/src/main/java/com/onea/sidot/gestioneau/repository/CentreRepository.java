package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.Centre;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Centre entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CentreRepository extends R2dbcRepository<Centre, Long>, CentreRepositoryInternal {
    Flux<Centre> findAllBy(Pageable pageable);

    @Query("SELECT * FROM centre entity WHERE entity.centreregroupement_id = :id")
    Flux<Centre> findByCentreregroupement(Long id);

    @Query("SELECT * FROM centre entity WHERE entity.centreregroupement_id IS NULL")
    Flux<Centre> findAllWhereCentreregroupementIsNull();

    @Query("SELECT * FROM centre entity WHERE entity.id not in (select centre_id from prevision)")
    Flux<Centre> findAllWherePrevisionIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Centre> findAll();

    @Override
    Mono<Centre> findById(Long id);

    @Override
    <S extends Centre> Mono<S> save(S entity);
}

interface CentreRepositoryInternal {
    <S extends Centre> Mono<S> insert(S entity);
    <S extends Centre> Mono<S> save(S entity);
    Mono<Integer> update(Centre entity);

    Flux<Centre> findAll();
    Mono<Centre> findById(Long id);
    Flux<Centre> findAllBy(Pageable pageable);
    Flux<Centre> findAllBy(Pageable pageable, Criteria criteria);
}
