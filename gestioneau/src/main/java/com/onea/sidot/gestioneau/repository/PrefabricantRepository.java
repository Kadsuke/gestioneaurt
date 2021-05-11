package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.Prefabricant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Prefabricant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PrefabricantRepository extends R2dbcRepository<Prefabricant, Long>, PrefabricantRepositoryInternal {
    Flux<Prefabricant> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<Prefabricant> findAll();

    @Override
    Mono<Prefabricant> findById(Long id);

    @Override
    <S extends Prefabricant> Mono<S> save(S entity);
}

interface PrefabricantRepositoryInternal {
    <S extends Prefabricant> Mono<S> insert(S entity);
    <S extends Prefabricant> Mono<S> save(S entity);
    Mono<Integer> update(Prefabricant entity);

    Flux<Prefabricant> findAll();
    Mono<Prefabricant> findById(Long id);
    Flux<Prefabricant> findAllBy(Pageable pageable);
    Flux<Prefabricant> findAllBy(Pageable pageable, Criteria criteria);
}
