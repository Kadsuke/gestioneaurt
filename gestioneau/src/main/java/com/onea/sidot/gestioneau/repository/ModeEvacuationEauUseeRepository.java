package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.ModeEvacuationEauUsee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the ModeEvacuationEauUsee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ModeEvacuationEauUseeRepository
    extends R2dbcRepository<ModeEvacuationEauUsee, Long>, ModeEvacuationEauUseeRepositoryInternal {
    Flux<ModeEvacuationEauUsee> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<ModeEvacuationEauUsee> findAll();

    @Override
    Mono<ModeEvacuationEauUsee> findById(Long id);

    @Override
    <S extends ModeEvacuationEauUsee> Mono<S> save(S entity);
}

interface ModeEvacuationEauUseeRepositoryInternal {
    <S extends ModeEvacuationEauUsee> Mono<S> insert(S entity);
    <S extends ModeEvacuationEauUsee> Mono<S> save(S entity);
    Mono<Integer> update(ModeEvacuationEauUsee entity);

    Flux<ModeEvacuationEauUsee> findAll();
    Mono<ModeEvacuationEauUsee> findById(Long id);
    Flux<ModeEvacuationEauUsee> findAllBy(Pageable pageable);
    Flux<ModeEvacuationEauUsee> findAllBy(Pageable pageable, Criteria criteria);
}
