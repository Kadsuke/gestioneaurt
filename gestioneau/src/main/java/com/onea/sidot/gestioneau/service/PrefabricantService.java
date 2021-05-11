package com.onea.sidot.gestioneau.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.onea.sidot.gestioneau.domain.Prefabricant;
import com.onea.sidot.gestioneau.repository.PrefabricantRepository;
import com.onea.sidot.gestioneau.repository.search.PrefabricantSearchRepository;
import com.onea.sidot.gestioneau.service.dto.PrefabricantDTO;
import com.onea.sidot.gestioneau.service.mapper.PrefabricantMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Prefabricant}.
 */
@Service
@Transactional
public class PrefabricantService {

    private final Logger log = LoggerFactory.getLogger(PrefabricantService.class);

    private final PrefabricantRepository prefabricantRepository;

    private final PrefabricantMapper prefabricantMapper;

    private final PrefabricantSearchRepository prefabricantSearchRepository;

    public PrefabricantService(
        PrefabricantRepository prefabricantRepository,
        PrefabricantMapper prefabricantMapper,
        PrefabricantSearchRepository prefabricantSearchRepository
    ) {
        this.prefabricantRepository = prefabricantRepository;
        this.prefabricantMapper = prefabricantMapper;
        this.prefabricantSearchRepository = prefabricantSearchRepository;
    }

    /**
     * Save a prefabricant.
     *
     * @param prefabricantDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PrefabricantDTO> save(PrefabricantDTO prefabricantDTO) {
        log.debug("Request to save Prefabricant : {}", prefabricantDTO);
        return prefabricantRepository
            .save(prefabricantMapper.toEntity(prefabricantDTO))
            .flatMap(prefabricantSearchRepository::save)
            .map(prefabricantMapper::toDto);
    }

    /**
     * Partially update a prefabricant.
     *
     * @param prefabricantDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<PrefabricantDTO> partialUpdate(PrefabricantDTO prefabricantDTO) {
        log.debug("Request to partially update Prefabricant : {}", prefabricantDTO);

        return prefabricantRepository
            .findById(prefabricantDTO.getId())
            .map(
                existingPrefabricant -> {
                    prefabricantMapper.partialUpdate(existingPrefabricant, prefabricantDTO);
                    return existingPrefabricant;
                }
            )
            .flatMap(prefabricantRepository::save)
            .flatMap(
                savedPrefabricant -> {
                    prefabricantSearchRepository.save(savedPrefabricant);

                    return Mono.just(savedPrefabricant);
                }
            )
            .map(prefabricantMapper::toDto);
    }

    /**
     * Get all the prefabricants.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PrefabricantDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Prefabricants");
        return prefabricantRepository.findAllBy(pageable).map(prefabricantMapper::toDto);
    }

    /**
     * Returns the number of prefabricants available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return prefabricantRepository.count();
    }

    /**
     * Returns the number of prefabricants available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return prefabricantSearchRepository.count();
    }

    /**
     * Get one prefabricant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<PrefabricantDTO> findOne(Long id) {
        log.debug("Request to get Prefabricant : {}", id);
        return prefabricantRepository.findById(id).map(prefabricantMapper::toDto);
    }

    /**
     * Delete the prefabricant by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Prefabricant : {}", id);
        return prefabricantRepository.deleteById(id).then(prefabricantSearchRepository.deleteById(id));
    }

    /**
     * Search for the prefabricant corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PrefabricantDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Prefabricants for query {}", query);
        return prefabricantSearchRepository.search(query, pageable).map(prefabricantMapper::toDto);
    }
}
