package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.Localite;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Localite entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LocaliteRepository extends R2dbcRepository<Localite, Long>, LocaliteRepositoryInternal {
    Flux<Localite> findAllBy(Pageable pageable);

    @Query("SELECT * FROM localite entity WHERE entity.commune_id = :id")
    Flux<Localite> findByCommune(Long id);

    @Query("SELECT * FROM localite entity WHERE entity.commune_id IS NULL")
    Flux<Localite> findAllWhereCommuneIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Localite> findAll();

    @Override
    Mono<Localite> findById(Long id);

    @Override
    <S extends Localite> Mono<S> save(S entity);
}

interface LocaliteRepositoryInternal {
    <S extends Localite> Mono<S> insert(S entity);
    <S extends Localite> Mono<S> save(S entity);
    Mono<Integer> update(Localite entity);

    Flux<Localite> findAll();
    Mono<Localite> findById(Long id);
    Flux<Localite> findAllBy(Pageable pageable);
    Flux<Localite> findAllBy(Pageable pageable, Criteria criteria);
}
