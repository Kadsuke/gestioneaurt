package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.NatureOuvrage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the NatureOuvrage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NatureOuvrageRepository extends R2dbcRepository<NatureOuvrage, Long>, NatureOuvrageRepositoryInternal {
    Flux<NatureOuvrage> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<NatureOuvrage> findAll();

    @Override
    Mono<NatureOuvrage> findById(Long id);

    @Override
    <S extends NatureOuvrage> Mono<S> save(S entity);
}

interface NatureOuvrageRepositoryInternal {
    <S extends NatureOuvrage> Mono<S> insert(S entity);
    <S extends NatureOuvrage> Mono<S> save(S entity);
    Mono<Integer> update(NatureOuvrage entity);

    Flux<NatureOuvrage> findAll();
    Mono<NatureOuvrage> findById(Long id);
    Flux<NatureOuvrage> findAllBy(Pageable pageable);
    Flux<NatureOuvrage> findAllBy(Pageable pageable, Criteria criteria);
}
