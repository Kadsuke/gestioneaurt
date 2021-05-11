package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.TypeHabitation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the TypeHabitation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TypeHabitationRepository extends R2dbcRepository<TypeHabitation, Long>, TypeHabitationRepositoryInternal {
    Flux<TypeHabitation> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<TypeHabitation> findAll();

    @Override
    Mono<TypeHabitation> findById(Long id);

    @Override
    <S extends TypeHabitation> Mono<S> save(S entity);
}

interface TypeHabitationRepositoryInternal {
    <S extends TypeHabitation> Mono<S> insert(S entity);
    <S extends TypeHabitation> Mono<S> save(S entity);
    Mono<Integer> update(TypeHabitation entity);

    Flux<TypeHabitation> findAll();
    Mono<TypeHabitation> findById(Long id);
    Flux<TypeHabitation> findAllBy(Pageable pageable);
    Flux<TypeHabitation> findAllBy(Pageable pageable, Criteria criteria);
}
