package com.onea.sidot.gestioneau.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.onea.sidot.gestioneau.domain.ModeEvacExcreta;
import com.onea.sidot.gestioneau.repository.ModeEvacExcretaRepository;
import com.onea.sidot.gestioneau.repository.search.ModeEvacExcretaSearchRepository;
import com.onea.sidot.gestioneau.service.dto.ModeEvacExcretaDTO;
import com.onea.sidot.gestioneau.service.mapper.ModeEvacExcretaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ModeEvacExcreta}.
 */
@Service
@Transactional
public class ModeEvacExcretaService {

    private final Logger log = LoggerFactory.getLogger(ModeEvacExcretaService.class);

    private final ModeEvacExcretaRepository modeEvacExcretaRepository;

    private final ModeEvacExcretaMapper modeEvacExcretaMapper;

    private final ModeEvacExcretaSearchRepository modeEvacExcretaSearchRepository;

    public ModeEvacExcretaService(
        ModeEvacExcretaRepository modeEvacExcretaRepository,
        ModeEvacExcretaMapper modeEvacExcretaMapper,
        ModeEvacExcretaSearchRepository modeEvacExcretaSearchRepository
    ) {
        this.modeEvacExcretaRepository = modeEvacExcretaRepository;
        this.modeEvacExcretaMapper = modeEvacExcretaMapper;
        this.modeEvacExcretaSearchRepository = modeEvacExcretaSearchRepository;
    }

    /**
     * Save a modeEvacExcreta.
     *
     * @param modeEvacExcretaDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ModeEvacExcretaDTO> save(ModeEvacExcretaDTO modeEvacExcretaDTO) {
        log.debug("Request to save ModeEvacExcreta : {}", modeEvacExcretaDTO);
        return modeEvacExcretaRepository
            .save(modeEvacExcretaMapper.toEntity(modeEvacExcretaDTO))
            .flatMap(modeEvacExcretaSearchRepository::save)
            .map(modeEvacExcretaMapper::toDto);
    }

    /**
     * Partially update a modeEvacExcreta.
     *
     * @param modeEvacExcretaDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ModeEvacExcretaDTO> partialUpdate(ModeEvacExcretaDTO modeEvacExcretaDTO) {
        log.debug("Request to partially update ModeEvacExcreta : {}", modeEvacExcretaDTO);

        return modeEvacExcretaRepository
            .findById(modeEvacExcretaDTO.getId())
            .map(
                existingModeEvacExcreta -> {
                    modeEvacExcretaMapper.partialUpdate(existingModeEvacExcreta, modeEvacExcretaDTO);
                    return existingModeEvacExcreta;
                }
            )
            .flatMap(modeEvacExcretaRepository::save)
            .flatMap(
                savedModeEvacExcreta -> {
                    modeEvacExcretaSearchRepository.save(savedModeEvacExcreta);

                    return Mono.just(savedModeEvacExcreta);
                }
            )
            .map(modeEvacExcretaMapper::toDto);
    }

    /**
     * Get all the modeEvacExcretas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ModeEvacExcretaDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ModeEvacExcretas");
        return modeEvacExcretaRepository.findAllBy(pageable).map(modeEvacExcretaMapper::toDto);
    }

    /**
     * Returns the number of modeEvacExcretas available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return modeEvacExcretaRepository.count();
    }

    /**
     * Returns the number of modeEvacExcretas available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return modeEvacExcretaSearchRepository.count();
    }

    /**
     * Get one modeEvacExcreta by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ModeEvacExcretaDTO> findOne(Long id) {
        log.debug("Request to get ModeEvacExcreta : {}", id);
        return modeEvacExcretaRepository.findById(id).map(modeEvacExcretaMapper::toDto);
    }

    /**
     * Delete the modeEvacExcreta by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ModeEvacExcreta : {}", id);
        return modeEvacExcretaRepository.deleteById(id).then(modeEvacExcretaSearchRepository.deleteById(id));
    }

    /**
     * Search for the modeEvacExcreta corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ModeEvacExcretaDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ModeEvacExcretas for query {}", query);
        return modeEvacExcretaSearchRepository.search(query, pageable).map(modeEvacExcretaMapper::toDto);
    }
}
