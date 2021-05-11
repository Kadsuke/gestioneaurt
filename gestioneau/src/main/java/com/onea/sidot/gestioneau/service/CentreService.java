package com.onea.sidot.gestioneau.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.onea.sidot.gestioneau.domain.Centre;
import com.onea.sidot.gestioneau.repository.CentreRepository;
import com.onea.sidot.gestioneau.repository.search.CentreSearchRepository;
import com.onea.sidot.gestioneau.service.dto.CentreDTO;
import com.onea.sidot.gestioneau.service.mapper.CentreMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Centre}.
 */
@Service
@Transactional
public class CentreService {

    private final Logger log = LoggerFactory.getLogger(CentreService.class);

    private final CentreRepository centreRepository;

    private final CentreMapper centreMapper;

    private final CentreSearchRepository centreSearchRepository;

    public CentreService(CentreRepository centreRepository, CentreMapper centreMapper, CentreSearchRepository centreSearchRepository) {
        this.centreRepository = centreRepository;
        this.centreMapper = centreMapper;
        this.centreSearchRepository = centreSearchRepository;
    }

    /**
     * Save a centre.
     *
     * @param centreDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CentreDTO> save(CentreDTO centreDTO) {
        log.debug("Request to save Centre : {}", centreDTO);
        return centreRepository.save(centreMapper.toEntity(centreDTO)).flatMap(centreSearchRepository::save).map(centreMapper::toDto);
    }

    /**
     * Partially update a centre.
     *
     * @param centreDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CentreDTO> partialUpdate(CentreDTO centreDTO) {
        log.debug("Request to partially update Centre : {}", centreDTO);

        return centreRepository
            .findById(centreDTO.getId())
            .map(
                existingCentre -> {
                    centreMapper.partialUpdate(existingCentre, centreDTO);
                    return existingCentre;
                }
            )
            .flatMap(centreRepository::save)
            .flatMap(
                savedCentre -> {
                    centreSearchRepository.save(savedCentre);

                    return Mono.just(savedCentre);
                }
            )
            .map(centreMapper::toDto);
    }

    /**
     * Get all the centres.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CentreDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Centres");
        return centreRepository.findAllBy(pageable).map(centreMapper::toDto);
    }

    /**
     *  Get all the centres where Prevision is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CentreDTO> findAllWherePrevisionIsNull() {
        log.debug("Request to get all centres where Prevision is null");
        return centreRepository.findAllWherePrevisionIsNull().map(centreMapper::toDto);
    }

    /**
     * Returns the number of centres available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return centreRepository.count();
    }

    /**
     * Returns the number of centres available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return centreSearchRepository.count();
    }

    /**
     * Get one centre by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CentreDTO> findOne(Long id) {
        log.debug("Request to get Centre : {}", id);
        return centreRepository.findById(id).map(centreMapper::toDto);
    }

    /**
     * Delete the centre by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Centre : {}", id);
        return centreRepository.deleteById(id).then(centreSearchRepository.deleteById(id));
    }

    /**
     * Search for the centre corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CentreDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Centres for query {}", query);
        return centreSearchRepository.search(query, pageable).map(centreMapper::toDto);
    }
}
