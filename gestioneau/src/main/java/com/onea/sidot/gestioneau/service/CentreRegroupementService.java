package com.onea.sidot.gestioneau.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.onea.sidot.gestioneau.domain.CentreRegroupement;
import com.onea.sidot.gestioneau.repository.CentreRegroupementRepository;
import com.onea.sidot.gestioneau.repository.search.CentreRegroupementSearchRepository;
import com.onea.sidot.gestioneau.service.dto.CentreRegroupementDTO;
import com.onea.sidot.gestioneau.service.mapper.CentreRegroupementMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link CentreRegroupement}.
 */
@Service
@Transactional
public class CentreRegroupementService {

    private final Logger log = LoggerFactory.getLogger(CentreRegroupementService.class);

    private final CentreRegroupementRepository centreRegroupementRepository;

    private final CentreRegroupementMapper centreRegroupementMapper;

    private final CentreRegroupementSearchRepository centreRegroupementSearchRepository;

    public CentreRegroupementService(
        CentreRegroupementRepository centreRegroupementRepository,
        CentreRegroupementMapper centreRegroupementMapper,
        CentreRegroupementSearchRepository centreRegroupementSearchRepository
    ) {
        this.centreRegroupementRepository = centreRegroupementRepository;
        this.centreRegroupementMapper = centreRegroupementMapper;
        this.centreRegroupementSearchRepository = centreRegroupementSearchRepository;
    }

    /**
     * Save a centreRegroupement.
     *
     * @param centreRegroupementDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CentreRegroupementDTO> save(CentreRegroupementDTO centreRegroupementDTO) {
        log.debug("Request to save CentreRegroupement : {}", centreRegroupementDTO);
        return centreRegroupementRepository
            .save(centreRegroupementMapper.toEntity(centreRegroupementDTO))
            .flatMap(centreRegroupementSearchRepository::save)
            .map(centreRegroupementMapper::toDto);
    }

    /**
     * Partially update a centreRegroupement.
     *
     * @param centreRegroupementDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CentreRegroupementDTO> partialUpdate(CentreRegroupementDTO centreRegroupementDTO) {
        log.debug("Request to partially update CentreRegroupement : {}", centreRegroupementDTO);

        return centreRegroupementRepository
            .findById(centreRegroupementDTO.getId())
            .map(
                existingCentreRegroupement -> {
                    centreRegroupementMapper.partialUpdate(existingCentreRegroupement, centreRegroupementDTO);
                    return existingCentreRegroupement;
                }
            )
            .flatMap(centreRegroupementRepository::save)
            .flatMap(
                savedCentreRegroupement -> {
                    centreRegroupementSearchRepository.save(savedCentreRegroupement);

                    return Mono.just(savedCentreRegroupement);
                }
            )
            .map(centreRegroupementMapper::toDto);
    }

    /**
     * Get all the centreRegroupements.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CentreRegroupementDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CentreRegroupements");
        return centreRegroupementRepository.findAllBy(pageable).map(centreRegroupementMapper::toDto);
    }

    /**
     * Returns the number of centreRegroupements available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return centreRegroupementRepository.count();
    }

    /**
     * Returns the number of centreRegroupements available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return centreRegroupementSearchRepository.count();
    }

    /**
     * Get one centreRegroupement by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CentreRegroupementDTO> findOne(Long id) {
        log.debug("Request to get CentreRegroupement : {}", id);
        return centreRegroupementRepository.findById(id).map(centreRegroupementMapper::toDto);
    }

    /**
     * Delete the centreRegroupement by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete CentreRegroupement : {}", id);
        return centreRegroupementRepository.deleteById(id).then(centreRegroupementSearchRepository.deleteById(id));
    }

    /**
     * Search for the centreRegroupement corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CentreRegroupementDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of CentreRegroupements for query {}", query);
        return centreRegroupementSearchRepository.search(query, pageable).map(centreRegroupementMapper::toDto);
    }
}
