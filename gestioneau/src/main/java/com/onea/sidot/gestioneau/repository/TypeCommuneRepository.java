package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.TypeCommune;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the TypeCommune entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TypeCommuneRepository extends R2dbcRepository<TypeCommune, Long>, TypeCommuneRepositoryInternal {
    Flux<TypeCommune> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<TypeCommune> findAll();

    @Override
    Mono<TypeCommune> findById(Long id);

    @Override
    <S extends TypeCommune> Mono<S> save(S entity);
}

interface TypeCommuneRepositoryInternal {
    <S extends TypeCommune> Mono<S> insert(S entity);
    <S extends TypeCommune> Mono<S> save(S entity);
    Mono<Integer> update(TypeCommune entity);

    Flux<TypeCommune> findAll();
    Mono<TypeCommune> findById(Long id);
    Flux<TypeCommune> findAllBy(Pageable pageable);
    Flux<TypeCommune> findAllBy(Pageable pageable, Criteria criteria);
}
