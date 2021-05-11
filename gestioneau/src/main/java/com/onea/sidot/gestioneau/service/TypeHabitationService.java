package com.onea.sidot.gestioneau.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.onea.sidot.gestioneau.domain.TypeHabitation;
import com.onea.sidot.gestioneau.repository.TypeHabitationRepository;
import com.onea.sidot.gestioneau.repository.search.TypeHabitationSearchRepository;
import com.onea.sidot.gestioneau.service.dto.TypeHabitationDTO;
import com.onea.sidot.gestioneau.service.mapper.TypeHabitationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link TypeHabitation}.
 */
@Service
@Transactional
public class TypeHabitationService {

    private final Logger log = LoggerFactory.getLogger(TypeHabitationService.class);

    private final TypeHabitationRepository typeHabitationRepository;

    private final TypeHabitationMapper typeHabitationMapper;

    private final TypeHabitationSearchRepository typeHabitationSearchRepository;

    public TypeHabitationService(
        TypeHabitationRepository typeHabitationRepository,
        TypeHabitationMapper typeHabitationMapper,
        TypeHabitationSearchRepository typeHabitationSearchRepository
    ) {
        this.typeHabitationRepository = typeHabitationRepository;
        this.typeHabitationMapper = typeHabitationMapper;
        this.typeHabitationSearchRepository = typeHabitationSearchRepository;
    }

    /**
     * Save a typeHabitation.
     *
     * @param typeHabitationDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<TypeHabitationDTO> save(TypeHabitationDTO typeHabitationDTO) {
        log.debug("Request to save TypeHabitation : {}", typeHabitationDTO);
        return typeHabitationRepository
            .save(typeHabitationMapper.toEntity(typeHabitationDTO))
            .flatMap(typeHabitationSearchRepository::save)
            .map(typeHabitationMapper::toDto);
    }

    /**
     * Partially update a typeHabitation.
     *
     * @param typeHabitationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<TypeHabitationDTO> partialUpdate(TypeHabitationDTO typeHabitationDTO) {
        log.debug("Request to partially update TypeHabitation : {}", typeHabitationDTO);

        return typeHabitationRepository
            .findById(typeHabitationDTO.getId())
            .map(
                existingTypeHabitation -> {
                    typeHabitationMapper.partialUpdate(existingTypeHabitation, typeHabitationDTO);
                    return existingTypeHabitation;
                }
            )
            .flatMap(typeHabitationRepository::save)
            .flatMap(
                savedTypeHabitation -> {
                    typeHabitationSearchRepository.save(savedTypeHabitation);

                    return Mono.just(savedTypeHabitation);
                }
            )
            .map(typeHabitationMapper::toDto);
    }

    /**
     * Get all the typeHabitations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<TypeHabitationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TypeHabitations");
        return typeHabitationRepository.findAllBy(pageable).map(typeHabitationMapper::toDto);
    }

    /**
     * Returns the number of typeHabitations available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return typeHabitationRepository.count();
    }

    /**
     * Returns the number of typeHabitations available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return typeHabitationSearchRepository.count();
    }

    /**
     * Get one typeHabitation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<TypeHabitationDTO> findOne(Long id) {
        log.debug("Request to get TypeHabitation : {}", id);
        return typeHabitationRepository.findById(id).map(typeHabitationMapper::toDto);
    }

    /**
     * Delete the typeHabitation by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TypeHabitation : {}", id);
        return typeHabitationRepository.deleteById(id).then(typeHabitationSearchRepository.deleteById(id));
    }

    /**
     * Search for the typeHabitation corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<TypeHabitationDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TypeHabitations for query {}", query);
        return typeHabitationSearchRepository.search(query, pageable).map(typeHabitationMapper::toDto);
    }
}
