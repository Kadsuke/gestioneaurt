package com.onea.sidot.gestioneau.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.onea.sidot.gestioneau.domain.ModeEvacuationEauUsee;
import com.onea.sidot.gestioneau.repository.ModeEvacuationEauUseeRepository;
import com.onea.sidot.gestioneau.repository.search.ModeEvacuationEauUseeSearchRepository;
import com.onea.sidot.gestioneau.service.dto.ModeEvacuationEauUseeDTO;
import com.onea.sidot.gestioneau.service.mapper.ModeEvacuationEauUseeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ModeEvacuationEauUsee}.
 */
@Service
@Transactional
public class ModeEvacuationEauUseeService {

    private final Logger log = LoggerFactory.getLogger(ModeEvacuationEauUseeService.class);

    private final ModeEvacuationEauUseeRepository modeEvacuationEauUseeRepository;

    private final ModeEvacuationEauUseeMapper modeEvacuationEauUseeMapper;

    private final ModeEvacuationEauUseeSearchRepository modeEvacuationEauUseeSearchRepository;

    public ModeEvacuationEauUseeService(
        ModeEvacuationEauUseeRepository modeEvacuationEauUseeRepository,
        ModeEvacuationEauUseeMapper modeEvacuationEauUseeMapper,
        ModeEvacuationEauUseeSearchRepository modeEvacuationEauUseeSearchRepository
    ) {
        this.modeEvacuationEauUseeRepository = modeEvacuationEauUseeRepository;
        this.modeEvacuationEauUseeMapper = modeEvacuationEauUseeMapper;
        this.modeEvacuationEauUseeSearchRepository = modeEvacuationEauUseeSearchRepository;
    }

    /**
     * Save a modeEvacuationEauUsee.
     *
     * @param modeEvacuationEauUseeDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ModeEvacuationEauUseeDTO> save(ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO) {
        log.debug("Request to save ModeEvacuationEauUsee : {}", modeEvacuationEauUseeDTO);
        return modeEvacuationEauUseeRepository
            .save(modeEvacuationEauUseeMapper.toEntity(modeEvacuationEauUseeDTO))
            .flatMap(modeEvacuationEauUseeSearchRepository::save)
            .map(modeEvacuationEauUseeMapper::toDto);
    }

    /**
     * Partially update a modeEvacuationEauUsee.
     *
     * @param modeEvacuationEauUseeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ModeEvacuationEauUseeDTO> partialUpdate(ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO) {
        log.debug("Request to partially update ModeEvacuationEauUsee : {}", modeEvacuationEauUseeDTO);

        return modeEvacuationEauUseeRepository
            .findById(modeEvacuationEauUseeDTO.getId())
            .map(
                existingModeEvacuationEauUsee -> {
                    modeEvacuationEauUseeMapper.partialUpdate(existingModeEvacuationEauUsee, modeEvacuationEauUseeDTO);
                    return existingModeEvacuationEauUsee;
                }
            )
            .flatMap(modeEvacuationEauUseeRepository::save)
            .flatMap(
                savedModeEvacuationEauUsee -> {
                    modeEvacuationEauUseeSearchRepository.save(savedModeEvacuationEauUsee);

                    return Mono.just(savedModeEvacuationEauUsee);
                }
            )
            .map(modeEvacuationEauUseeMapper::toDto);
    }

    /**
     * Get all the modeEvacuationEauUsees.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ModeEvacuationEauUseeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ModeEvacuationEauUsees");
        return modeEvacuationEauUseeRepository.findAllBy(pageable).map(modeEvacuationEauUseeMapper::toDto);
    }

    /**
     * Returns the number of modeEvacuationEauUsees available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return modeEvacuationEauUseeRepository.count();
    }

    /**
     * Returns the number of modeEvacuationEauUsees available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return modeEvacuationEauUseeSearchRepository.count();
    }

    /**
     * Get one modeEvacuationEauUsee by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ModeEvacuationEauUseeDTO> findOne(Long id) {
        log.debug("Request to get ModeEvacuationEauUsee : {}", id);
        return modeEvacuationEauUseeRepository.findById(id).map(modeEvacuationEauUseeMapper::toDto);
    }

    /**
     * Delete the modeEvacuationEauUsee by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ModeEvacuationEauUsee : {}", id);
        return modeEvacuationEauUseeRepository.deleteById(id).then(modeEvacuationEauUseeSearchRepository.deleteById(id));
    }

    /**
     * Search for the modeEvacuationEauUsee corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ModeEvacuationEauUseeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ModeEvacuationEauUsees for query {}", query);
        return modeEvacuationEauUseeSearchRepository.search(query, pageable).map(modeEvacuationEauUseeMapper::toDto);
    }
}
