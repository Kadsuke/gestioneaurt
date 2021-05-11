package com.onea.sidot.gestioneau.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.onea.sidot.gestioneau.domain.NatureOuvrage;
import com.onea.sidot.gestioneau.repository.NatureOuvrageRepository;
import com.onea.sidot.gestioneau.repository.search.NatureOuvrageSearchRepository;
import com.onea.sidot.gestioneau.service.dto.NatureOuvrageDTO;
import com.onea.sidot.gestioneau.service.mapper.NatureOuvrageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link NatureOuvrage}.
 */
@Service
@Transactional
public class NatureOuvrageService {

    private final Logger log = LoggerFactory.getLogger(NatureOuvrageService.class);

    private final NatureOuvrageRepository natureOuvrageRepository;

    private final NatureOuvrageMapper natureOuvrageMapper;

    private final NatureOuvrageSearchRepository natureOuvrageSearchRepository;

    public NatureOuvrageService(
        NatureOuvrageRepository natureOuvrageRepository,
        NatureOuvrageMapper natureOuvrageMapper,
        NatureOuvrageSearchRepository natureOuvrageSearchRepository
    ) {
        this.natureOuvrageRepository = natureOuvrageRepository;
        this.natureOuvrageMapper = natureOuvrageMapper;
        this.natureOuvrageSearchRepository = natureOuvrageSearchRepository;
    }

    /**
     * Save a natureOuvrage.
     *
     * @param natureOuvrageDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<NatureOuvrageDTO> save(NatureOuvrageDTO natureOuvrageDTO) {
        log.debug("Request to save NatureOuvrage : {}", natureOuvrageDTO);
        return natureOuvrageRepository
            .save(natureOuvrageMapper.toEntity(natureOuvrageDTO))
            .flatMap(natureOuvrageSearchRepository::save)
            .map(natureOuvrageMapper::toDto);
    }

    /**
     * Partially update a natureOuvrage.
     *
     * @param natureOuvrageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<NatureOuvrageDTO> partialUpdate(NatureOuvrageDTO natureOuvrageDTO) {
        log.debug("Request to partially update NatureOuvrage : {}", natureOuvrageDTO);

        return natureOuvrageRepository
            .findById(natureOuvrageDTO.getId())
            .map(
                existingNatureOuvrage -> {
                    natureOuvrageMapper.partialUpdate(existingNatureOuvrage, natureOuvrageDTO);
                    return existingNatureOuvrage;
                }
            )
            .flatMap(natureOuvrageRepository::save)
            .flatMap(
                savedNatureOuvrage -> {
                    natureOuvrageSearchRepository.save(savedNatureOuvrage);

                    return Mono.just(savedNatureOuvrage);
                }
            )
            .map(natureOuvrageMapper::toDto);
    }

    /**
     * Get all the natureOuvrages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<NatureOuvrageDTO> findAll(Pageable pageable) {
        log.debug("Request to get all NatureOuvrages");
        return natureOuvrageRepository.findAllBy(pageable).map(natureOuvrageMapper::toDto);
    }

    /**
     * Returns the number of natureOuvrages available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return natureOuvrageRepository.count();
    }

    /**
     * Returns the number of natureOuvrages available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return natureOuvrageSearchRepository.count();
    }

    /**
     * Get one natureOuvrage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<NatureOuvrageDTO> findOne(Long id) {
        log.debug("Request to get NatureOuvrage : {}", id);
        return natureOuvrageRepository.findById(id).map(natureOuvrageMapper::toDto);
    }

    /**
     * Delete the natureOuvrage by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete NatureOuvrage : {}", id);
        return natureOuvrageRepository.deleteById(id).then(natureOuvrageSearchRepository.deleteById(id));
    }

    /**
     * Search for the natureOuvrage corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<NatureOuvrageDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of NatureOuvrages for query {}", query);
        return natureOuvrageSearchRepository.search(query, pageable).map(natureOuvrageMapper::toDto);
    }
}
