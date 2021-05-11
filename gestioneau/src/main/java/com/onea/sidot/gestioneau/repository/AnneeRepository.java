package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.Annee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Annee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnneeRepository extends R2dbcRepository<Annee, Long>, AnneeRepositoryInternal {
    Flux<Annee> findAllBy(Pageable pageable);

    @Query("SELECT * FROM annee entity WHERE entity.id not in (select refannee_id from prevision)")
    Flux<Annee> findAllWherePrevisionIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Annee> findAll();

    @Override
    Mono<Annee> findById(Long id);

    @Override
    <S extends Annee> Mono<S> save(S entity);
}

interface AnneeRepositoryInternal {
    <S extends Annee> Mono<S> insert(S entity);
    <S extends Annee> Mono<S> save(S entity);
    Mono<Integer> update(Annee entity);

    Flux<Annee> findAll();
    Mono<Annee> findById(Long id);
    Flux<Annee> findAllBy(Pageable pageable);
    Flux<Annee> findAllBy(Pageable pageable, Criteria criteria);
}
