package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.CentreRegroupement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the CentreRegroupement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CentreRegroupementRepository extends R2dbcRepository<CentreRegroupement, Long>, CentreRegroupementRepositoryInternal {
    Flux<CentreRegroupement> findAllBy(Pageable pageable);

    @Query("SELECT * FROM centre_regroupement entity WHERE entity.directionregionale_id = :id")
    Flux<CentreRegroupement> findByDirectionregionale(Long id);

    @Query("SELECT * FROM centre_regroupement entity WHERE entity.directionregionale_id IS NULL")
    Flux<CentreRegroupement> findAllWhereDirectionregionaleIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<CentreRegroupement> findAll();

    @Override
    Mono<CentreRegroupement> findById(Long id);

    @Override
    <S extends CentreRegroupement> Mono<S> save(S entity);
}

interface CentreRegroupementRepositoryInternal {
    <S extends CentreRegroupement> Mono<S> insert(S entity);
    <S extends CentreRegroupement> Mono<S> save(S entity);
    Mono<Integer> update(CentreRegroupement entity);

    Flux<CentreRegroupement> findAll();
    Mono<CentreRegroupement> findById(Long id);
    Flux<CentreRegroupement> findAllBy(Pageable pageable);
    Flux<CentreRegroupement> findAllBy(Pageable pageable, Criteria criteria);
}
