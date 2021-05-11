package com.onea.sidot.gestioneau.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.onea.sidot.gestioneau.domain.Annee;
import com.onea.sidot.gestioneau.repository.AnneeRepository;
import com.onea.sidot.gestioneau.repository.search.AnneeSearchRepository;
import com.onea.sidot.gestioneau.service.dto.AnneeDTO;
import com.onea.sidot.gestioneau.service.mapper.AnneeMapper;
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
 * Service Implementation for managing {@link Annee}.
 */
@Service
@Transactional
public class AnneeService {

    private final Logger log = LoggerFactory.getLogger(AnneeService.class);

    private final AnneeRepository anneeRepository;

    private final AnneeMapper anneeMapper;

    private final AnneeSearchRepository anneeSearchRepository;

    public AnneeService(AnneeRepository anneeRepository, AnneeMapper anneeMapper, AnneeSearchRepository anneeSearchRepository) {
        this.anneeRepository = anneeRepository;
        this.anneeMapper = anneeMapper;
        this.anneeSearchRepository = anneeSearchRepository;
    }

    /**
     * Save a annee.
     *
     * @param anneeDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AnneeDTO> save(AnneeDTO anneeDTO) {
        log.debug("Request to save Annee : {}", anneeDTO);
        return anneeRepository.save(anneeMapper.toEntity(anneeDTO)).flatMap(anneeSearchRepository::save).map(anneeMapper::toDto);
    }

    /**
     * Partially update a annee.
     *
     * @param anneeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<AnneeDTO> partialUpdate(AnneeDTO anneeDTO) {
        log.debug("Request to partially update Annee : {}", anneeDTO);

        return anneeRepository
            .findById(anneeDTO.getId())
            .map(
                existingAnnee -> {
                    anneeMapper.partialUpdate(existingAnnee, anneeDTO);
                    return existingAnnee;
                }
            )
            .flatMap(anneeRepository::save)
            .flatMap(
                savedAnnee -> {
                    anneeSearchRepository.save(savedAnnee);

                    return Mono.just(savedAnnee);
                }
            )
            .map(anneeMapper::toDto);
    }

    /**
     * Get all the annees.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AnneeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Annees");
        return anneeRepository.findAllBy(pageable).map(anneeMapper::toDto);
    }

    /**
     *  Get all the annees where Prevision is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AnneeDTO> findAllWherePrevisionIsNull() {
        log.debug("Request to get all annees where Prevision is null");
        return anneeRepository.findAllWherePrevisionIsNull().map(anneeMapper::toDto);
    }

    /**
     * Returns the number of annees available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return anneeRepository.count();
    }

    /**
     * Returns the number of annees available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return anneeSearchRepository.count();
    }

    /**
     * Get one annee by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<AnneeDTO> findOne(Long id) {
        log.debug("Request to get Annee : {}", id);
        return anneeRepository.findById(id).map(anneeMapper::toDto);
    }

    /**
     * Delete the annee by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Annee : {}", id);
        return anneeRepository.deleteById(id).then(anneeSearchRepository.deleteById(id));
    }

    /**
     * Search for the annee corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AnneeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Annees for query {}", query);
        return anneeSearchRepository.search(query, pageable).map(anneeMapper::toDto);
    }
}
