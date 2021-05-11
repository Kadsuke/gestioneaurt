package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.Secteur;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Secteur entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SecteurRepository extends R2dbcRepository<Secteur, Long>, SecteurRepositoryInternal {
    Flux<Secteur> findAllBy(Pageable pageable);

    @Query("SELECT * FROM secteur entity WHERE entity.localite_id = :id")
    Flux<Secteur> findByLocalite(Long id);

    @Query("SELECT * FROM secteur entity WHERE entity.localite_id IS NULL")
    Flux<Secteur> findAllWhereLocaliteIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Secteur> findAll();

    @Override
    Mono<Secteur> findById(Long id);

    @Override
    <S extends Secteur> Mono<S> save(S entity);
}

interface SecteurRepositoryInternal {
    <S extends Secteur> Mono<S> insert(S entity);
    <S extends Secteur> Mono<S> save(S entity);
    Mono<Integer> update(Secteur entity);

    Flux<Secteur> findAll();
    Mono<Secteur> findById(Long id);
    Flux<Secteur> findAllBy(Pageable pageable);
    Flux<Secteur> findAllBy(Pageable pageable, Criteria criteria);
}
