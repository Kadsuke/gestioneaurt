package com.onea.sidot.gestioneau.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.onea.sidot.gestioneau.domain.Section;
import com.onea.sidot.gestioneau.repository.SectionRepository;
import com.onea.sidot.gestioneau.repository.search.SectionSearchRepository;
import com.onea.sidot.gestioneau.service.dto.SectionDTO;
import com.onea.sidot.gestioneau.service.mapper.SectionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Section}.
 */
@Service
@Transactional
public class SectionService {

    private final Logger log = LoggerFactory.getLogger(SectionService.class);

    private final SectionRepository sectionRepository;

    private final SectionMapper sectionMapper;

    private final SectionSearchRepository sectionSearchRepository;

    public SectionService(
        SectionRepository sectionRepository,
        SectionMapper sectionMapper,
        SectionSearchRepository sectionSearchRepository
    ) {
        this.sectionRepository = sectionRepository;
        this.sectionMapper = sectionMapper;
        this.sectionSearchRepository = sectionSearchRepository;
    }

    /**
     * Save a section.
     *
     * @param sectionDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<SectionDTO> save(SectionDTO sectionDTO) {
        log.debug("Request to save Section : {}", sectionDTO);
        return sectionRepository.save(sectionMapper.toEntity(sectionDTO)).flatMap(sectionSearchRepository::save).map(sectionMapper::toDto);
    }

    /**
     * Partially update a section.
     *
     * @param sectionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<SectionDTO> partialUpdate(SectionDTO sectionDTO) {
        log.debug("Request to partially update Section : {}", sectionDTO);

        return sectionRepository
            .findById(sectionDTO.getId())
            .map(
                existingSection -> {
                    sectionMapper.partialUpdate(existingSection, sectionDTO);
                    return existingSection;
                }
            )
            .flatMap(sectionRepository::save)
            .flatMap(
                savedSection -> {
                    sectionSearchRepository.save(savedSection);

                    return Mono.just(savedSection);
                }
            )
            .map(sectionMapper::toDto);
    }

    /**
     * Get all the sections.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<SectionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Sections");
        return sectionRepository.findAllBy(pageable).map(sectionMapper::toDto);
    }

    /**
     * Returns the number of sections available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return sectionRepository.count();
    }

    /**
     * Returns the number of sections available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return sectionSearchRepository.count();
    }

    /**
     * Get one section by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<SectionDTO> findOne(Long id) {
        log.debug("Request to get Section : {}", id);
        return sectionRepository.findById(id).map(sectionMapper::toDto);
    }

    /**
     * Delete the section by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Section : {}", id);
        return sectionRepository.deleteById(id).then(sectionSearchRepository.deleteById(id));
    }

    /**
     * Search for the section corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<SectionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Sections for query {}", query);
        return sectionSearchRepository.search(query, pageable).map(sectionMapper::toDto);
    }
}
