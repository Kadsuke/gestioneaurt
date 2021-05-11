package com.onea.sidot.gestioneau.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.onea.sidot.gestioneau.domain.SourceApprovEp;
import com.onea.sidot.gestioneau.repository.SourceApprovEpRepository;
import com.onea.sidot.gestioneau.repository.search.SourceApprovEpSearchRepository;
import com.onea.sidot.gestioneau.service.dto.SourceApprovEpDTO;
import com.onea.sidot.gestioneau.service.mapper.SourceApprovEpMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link SourceApprovEp}.
 */
@Service
@Transactional
public class SourceApprovEpService {

    private final Logger log = LoggerFactory.getLogger(SourceApprovEpService.class);

    private final SourceApprovEpRepository sourceApprovEpRepository;

    private final SourceApprovEpMapper sourceApprovEpMapper;

    private final SourceApprovEpSearchRepository sourceApprovEpSearchRepository;

    public SourceApprovEpService(
        SourceApprovEpRepository sourceApprovEpRepository,
        SourceApprovEpMapper sourceApprovEpMapper,
        SourceApprovEpSearchRepository sourceApprovEpSearchRepository
    ) {
        this.sourceApprovEpRepository = sourceApprovEpRepository;
        this.sourceApprovEpMapper = sourceApprovEpMapper;
        this.sourceApprovEpSearchRepository = sourceApprovEpSearchRepository;
    }

    /**
     * Save a sourceApprovEp.
     *
     * @param sourceApprovEpDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<SourceApprovEpDTO> save(SourceApprovEpDTO sourceApprovEpDTO) {
        log.debug("Request to save SourceApprovEp : {}", sourceApprovEpDTO);
        return sourceApprovEpRepository
            .save(sourceApprovEpMapper.toEntity(sourceApprovEpDTO))
            .flatMap(sourceApprovEpSearchRepository::save)
            .map(sourceApprovEpMapper::toDto);
    }

    /**
     * Partially update a sourceApprovEp.
     *
     * @param sourceApprovEpDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<SourceApprovEpDTO> partialUpdate(SourceApprovEpDTO sourceApprovEpDTO) {
        log.debug("Request to partially update SourceApprovEp : {}", sourceApprovEpDTO);

        return sourceApprovEpRepository
            .findById(sourceApprovEpDTO.getId())
            .map(
                existingSourceApprovEp -> {
                    sourceApprovEpMapper.partialUpdate(existingSourceApprovEp, sourceApprovEpDTO);
                    return existingSourceApprovEp;
                }
            )
            .flatMap(sourceApprovEpRepository::save)
            .flatMap(
                savedSourceApprovEp -> {
                    sourceApprovEpSearchRepository.save(savedSourceApprovEp);

                    return Mono.just(savedSourceApprovEp);
                }
            )
            .map(sourceApprovEpMapper::toDto);
    }

    /**
     * Get all the sourceApprovEps.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<SourceApprovEpDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SourceApprovEps");
        return sourceApprovEpRepository.findAllBy(pageable).map(sourceApprovEpMapper::toDto);
    }

    /**
     * Returns the number of sourceApprovEps available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return sourceApprovEpRepository.count();
    }

    /**
     * Returns the number of sourceApprovEps available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return sourceApprovEpSearchRepository.count();
    }

    /**
     * Get one sourceApprovEp by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<SourceApprovEpDTO> findOne(Long id) {
        log.debug("Request to get SourceApprovEp : {}", id);
        return sourceApprovEpRepository.findById(id).map(sourceApprovEpMapper::toDto);
    }

    /**
     * Delete the sourceApprovEp by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete SourceApprovEp : {}", id);
        return sourceApprovEpRepository.deleteById(id).then(sourceApprovEpSearchRepository.deleteById(id));
    }

    /**
     * Search for the sourceApprovEp corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<SourceApprovEpDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SourceApprovEps for query {}", query);
        return sourceApprovEpSearchRepository.search(query, pageable).map(sourceApprovEpMapper::toDto);
    }
}
