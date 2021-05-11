package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.Province;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Province entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProvinceRepository extends R2dbcRepository<Province, Long>, ProvinceRepositoryInternal {
    Flux<Province> findAllBy(Pageable pageable);

    @Query("SELECT * FROM province entity WHERE entity.region_id = :id")
    Flux<Province> findByRegion(Long id);

    @Query("SELECT * FROM province entity WHERE entity.region_id IS NULL")
    Flux<Province> findAllWhereRegionIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Province> findAll();

    @Override
    Mono<Province> findById(Long id);

    @Override
    <S extends Province> Mono<S> save(S entity);
}

interface ProvinceRepositoryInternal {
    <S extends Province> Mono<S> insert(S entity);
    <S extends Province> Mono<S> save(S entity);
    Mono<Integer> update(Province entity);

    Flux<Province> findAll();
    Mono<Province> findById(Long id);
    Flux<Province> findAllBy(Pageable pageable);
    Flux<Province> findAllBy(Pageable pageable, Criteria criteria);
}
