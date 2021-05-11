package com.onea.sidot.gestioneau.repository;

import com.onea.sidot.gestioneau.domain.FicheSuiviOuvrage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the FicheSuiviOuvrage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FicheSuiviOuvrageRepository extends R2dbcRepository<FicheSuiviOuvrage, Long>, FicheSuiviOuvrageRepositoryInternal {
    Flux<FicheSuiviOuvrage> findAllBy(Pageable pageable);

    @Query("SELECT * FROM fiche_suivi_ouvrage entity WHERE entity.parcelle_id = :id")
    Flux<FicheSuiviOuvrage> findByParcelle(Long id);

    @Query("SELECT * FROM fiche_suivi_ouvrage entity WHERE entity.parcelle_id IS NULL")
    Flux<FicheSuiviOuvrage> findAllWhereParcelleIsNull();

    @Query("SELECT * FROM fiche_suivi_ouvrage entity WHERE entity.prevision_id = :id")
    Flux<FicheSuiviOuvrage> findByPrevision(Long id);

    @Query("SELECT * FROM fiche_suivi_ouvrage entity WHERE entity.prevision_id IS NULL")
    Flux<FicheSuiviOuvrage> findAllWherePrevisionIsNull();

    @Query("SELECT * FROM fiche_suivi_ouvrage entity WHERE entity.natureouvrage_id = :id")
    Flux<FicheSuiviOuvrage> findByNatureouvrage(Long id);

    @Query("SELECT * FROM fiche_suivi_ouvrage entity WHERE entity.natureouvrage_id IS NULL")
    Flux<FicheSuiviOuvrage> findAllWhereNatureouvrageIsNull();

    @Query("SELECT * FROM fiche_suivi_ouvrage entity WHERE entity.typehabitation_id = :id")
    Flux<FicheSuiviOuvrage> findByTypehabitation(Long id);

    @Query("SELECT * FROM fiche_suivi_ouvrage entity WHERE entity.typehabitation_id IS NULL")
    Flux<FicheSuiviOuvrage> findAllWhereTypehabitationIsNull();

    @Query("SELECT * FROM fiche_suivi_ouvrage entity WHERE entity.sourceapprovep_id = :id")
    Flux<FicheSuiviOuvrage> findBySourceapprovep(Long id);

    @Query("SELECT * FROM fiche_suivi_ouvrage entity WHERE entity.sourceapprovep_id IS NULL")
    Flux<FicheSuiviOuvrage> findAllWhereSourceapprovepIsNull();

    @Query("SELECT * FROM fiche_suivi_ouvrage entity WHERE entity.modeevacuationeauusee_id = :id")
    Flux<FicheSuiviOuvrage> findByModeevacuationeauusee(Long id);

    @Query("SELECT * FROM fiche_suivi_ouvrage entity WHERE entity.modeevacuationeauusee_id IS NULL")
    Flux<FicheSuiviOuvrage> findAllWhereModeevacuationeauuseeIsNull();

    @Query("SELECT * FROM fiche_suivi_ouvrage entity WHERE entity.modeevacexcreta_id = :id")
    Flux<FicheSuiviOuvrage> findByModeevacexcreta(Long id);

    @Query("SELECT * FROM fiche_suivi_ouvrage entity WHERE entity.modeevacexcreta_id IS NULL")
    Flux<FicheSuiviOuvrage> findAllWhereModeevacexcretaIsNull();

    @Query("SELECT * FROM fiche_suivi_ouvrage entity WHERE entity.macon_id = :id")
    Flux<FicheSuiviOuvrage> findByMacon(Long id);

    @Query("SELECT * FROM fiche_suivi_ouvrage entity WHERE entity.macon_id IS NULL")
    Flux<FicheSuiviOuvrage> findAllWhereMaconIsNull();

    @Query("SELECT * FROM fiche_suivi_ouvrage entity WHERE entity.prefabricant_id = :id")
    Flux<FicheSuiviOuvrage> findByPrefabricant(Long id);

    @Query("SELECT * FROM fiche_suivi_ouvrage entity WHERE entity.prefabricant_id IS NULL")
    Flux<FicheSuiviOuvrage> findAllWherePrefabricantIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<FicheSuiviOuvrage> findAll();

    @Override
    Mono<FicheSuiviOuvrage> findById(Long id);

    @Override
    <S extends FicheSuiviOuvrage> Mono<S> save(S entity);
}

interface FicheSuiviOuvrageRepositoryInternal {
    <S extends FicheSuiviOuvrage> Mono<S> insert(S entity);
    <S extends FicheSuiviOuvrage> Mono<S> save(S entity);
    Mono<Integer> update(FicheSuiviOuvrage entity);

    Flux<FicheSuiviOuvrage> findAll();
    Mono<FicheSuiviOuvrage> findById(Long id);
    Flux<FicheSuiviOuvrage> findAllBy(Pageable pageable);
    Flux<FicheSuiviOuvrage> findAllBy(Pageable pageable, Criteria criteria);
}
