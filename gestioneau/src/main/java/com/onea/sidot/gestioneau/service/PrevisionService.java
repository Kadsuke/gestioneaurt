package com.onea.sidot.gestioneau.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.onea.sidot.gestioneau.domain.Prevision;
import com.onea.sidot.gestioneau.repository.PrevisionRepository;
import com.onea.sidot.gestioneau.repository.search.PrevisionSearchRepository;
import com.onea.sidot.gestioneau.service.dto.PrevisionDTO;
import com.onea.sidot.gestioneau.service.mapper.PrevisionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Prevision}.
 */
@Service
@Transactional
public class PrevisionService {

    private final Logger log = LoggerFactory.getLogger(PrevisionService.class);

    private final PrevisionRepository previsionRepository;

    private final PrevisionMapper previsionMapper;

    private final PrevisionSearchRepository previsionSearchRepository;

    public PrevisionService(
        PrevisionRepository previsionRepository,
        PrevisionMapper previsionMapper,
        PrevisionSearchRepository previsionSearchRepository
    ) {
        this.previsionRepository = previsionRepository;
        this.previsionMapper = previsionMapper;
        this.previsionSearchRepository = previsionSearchRepository;
    }

    /**
     * Save a prevision.
     *
     * @param previsionDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PrevisionDTO> save(PrevisionDTO previsionDTO) {
        log.debug("Request to save Prevision : {}", previsionDTO);
        return previsionRepository
            .save(previsionMapper.toEntity(previsionDTO))
            .flatMap(previsionSearchRepository::save)
            .map(previsionMapper::toDto);
    }

    /**
     * Partially update a prevision.
     *
     * @param previsionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<PrevisionDTO> partialUpdate(PrevisionDTO previsionDTO) {
        log.debug("Request to partially update Prevision : {}", previsionDTO);

        return previsionRepository
            .findById(previsionDTO.getId())
            .map(
                existingPrevision -> {
                    previsionMapper.partialUpdate(existingPrevision, previsionDTO);
                    return existingPrevision;
                }
            )
            .flatMap(previsionRepository::save)
            .flatMap(
                savedPrevision -> {
                    previsionSearchRepository.save(savedPrevision);

                    return Mono.just(savedPrevision);
                }
            )
            .map(previsionMapper::toDto);
    }

    /**
     * Get all the previsions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PrevisionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Previsions");
        return previsionRepository.findAllBy(pageable).map(previsionMapper::toDto);
    }

    /**
     * Returns the number of previsions available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return previsionRepository.count();
    }

    /**
     * Returns the number of previsions available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return previsionSearchRepository.count();
    }

    /**
     * Get one prevision by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<PrevisionDTO> findOne(Long id) {
        log.debug("Request to get Prevision : {}", id);
        return previsionRepository.findById(id).map(previsionMapper::toDto);
    }

    /**
     * Delete the prevision by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Prevision : {}", id);
        return previsionRepository.deleteById(id).then(previsionSearchRepository.deleteById(id));
    }

    /**
     * Search for the prevision corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PrevisionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Previsions for query {}", query);
        return previsionSearchRepository.search(query, pageable).map(previsionMapper::toDto);
    }
}
