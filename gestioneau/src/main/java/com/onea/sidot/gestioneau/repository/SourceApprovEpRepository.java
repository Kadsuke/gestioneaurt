package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.SourceApprovEp;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the SourceApprovEp entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SourceApprovEpRepository extends R2dbcRepository<SourceApprovEp, Long>, SourceApprovEpRepositoryInternal {
    Flux<SourceApprovEp> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<SourceApprovEp> findAll();

    @Override
    Mono<SourceApprovEp> findById(Long id);

    @Override
    <S extends SourceApprovEp> Mono<S> save(S entity);
}

interface SourceApprovEpRepositoryInternal {
    <S extends SourceApprovEp> Mono<S> insert(S entity);
    <S extends SourceApprovEp> Mono<S> save(S entity);
    Mono<Integer> update(SourceApprovEp entity);

    Flux<SourceApprovEp> findAll();
    Mono<SourceApprovEp> findById(Long id);
    Flux<SourceApprovEp> findAllBy(Pageable pageable);
    Flux<SourceApprovEp> findAllBy(Pageable pageable, Criteria criteria);
}
