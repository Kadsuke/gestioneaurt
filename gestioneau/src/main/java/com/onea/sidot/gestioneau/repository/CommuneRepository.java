package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.Commune;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Commune entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommuneRepository extends R2dbcRepository<Commune, Long>, CommuneRepositoryInternal {
    Flux<Commune> findAllBy(Pageable pageable);

    @Query("SELECT * FROM commune entity WHERE entity.province_id = :id")
    Flux<Commune> findByProvince(Long id);

    @Query("SELECT * FROM commune entity WHERE entity.province_id IS NULL")
    Flux<Commune> findAllWhereProvinceIsNull();

    @Query("SELECT * FROM commune entity WHERE entity.typecommune_id = :id")
    Flux<Commune> findByTypecommune(Long id);

    @Query("SELECT * FROM commune entity WHERE entity.typecommune_id IS NULL")
    Flux<Commune> findAllWhereTypecommuneIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Commune> findAll();

    @Override
    Mono<Commune> findById(Long id);

    @Override
    <S extends Commune> Mono<S> save(S entity);
}

interface CommuneRepositoryInternal {
    <S extends Commune> Mono<S> insert(S entity);
    <S extends Commune> Mono<S> save(S entity);
    Mono<Integer> update(Commune entity);

    Flux<Commune> findAll();
    Mono<Commune> findById(Long id);
    Flux<Commune> findAllBy(Pageable pageable);
    Flux<Commune> findAllBy(Pageable pageable, Criteria criteria);
}
