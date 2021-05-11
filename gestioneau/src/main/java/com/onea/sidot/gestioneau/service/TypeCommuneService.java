package com.onea.sidot.gestioneau.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.onea.sidot.gestioneau.domain.TypeCommune;
import com.onea.sidot.gestioneau.repository.TypeCommuneRepository;
import com.onea.sidot.gestioneau.repository.search.TypeCommuneSearchRepository;
import com.onea.sidot.gestioneau.service.dto.TypeCommuneDTO;
import com.onea.sidot.gestioneau.service.mapper.TypeCommuneMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link TypeCommune}.
 */
@Service
@Transactional
public class TypeCommuneService {

    private final Logger log = LoggerFactory.getLogger(TypeCommuneService.class);

    private final TypeCommuneRepository typeCommuneRepository;

    private final TypeCommuneMapper typeCommuneMapper;

    private final TypeCommuneSearchRepository typeCommuneSearchRepository;

    public TypeCommuneService(
        TypeCommuneRepository typeCommuneRepository,
        TypeCommuneMapper typeCommuneMapper,
        TypeCommuneSearchRepository typeCommuneSearchRepository
    ) {
        this.typeCommuneRepository = typeCommuneRepository;
        this.typeCommuneMapper = typeCommuneMapper;
        this.typeCommuneSearchRepository = typeCommuneSearchRepository;
    }

    /**
     * Save a typeCommune.
     *
     * @param typeCommuneDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<TypeCommuneDTO> save(TypeCommuneDTO typeCommuneDTO) {
        log.debug("Request to save TypeCommune : {}", typeCommuneDTO);
        return typeCommuneRepository
            .save(typeCommuneMapper.toEntity(typeCommuneDTO))
            .flatMap(typeCommuneSearchRepository::save)
            .map(typeCommuneMapper::toDto);
    }

    /**
     * Partially update a typeCommune.
     *
     * @param typeCommuneDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<TypeCommuneDTO> partialUpdate(TypeCommuneDTO typeCommuneDTO) {
        log.debug("Request to partially update TypeCommune : {}", typeCommuneDTO);

        return typeCommuneRepository
            .findById(typeCommuneDTO.getId())
            .map(
                existingTypeCommune -> {
                    typeCommuneMapper.partialUpdate(existingTypeCommune, typeCommuneDTO);
                    return existingTypeCommune;
                }
            )
            .flatMap(typeCommuneRepository::save)
            .flatMap(
                savedTypeCommune -> {
                    typeCommuneSearchRepository.save(savedTypeCommune);

                    return Mono.just(savedTypeCommune);
                }
            )
            .map(typeCommuneMapper::toDto);
    }

    /**
     * Get all the typeCommunes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<TypeCommuneDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TypeCommunes");
        return typeCommuneRepository.findAllBy(pageable).map(typeCommuneMapper::toDto);
    }

    /**
     * Returns the number of typeCommunes available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return typeCommuneRepository.count();
    }

    /**
     * Returns the number of typeCommunes available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return typeCommuneSearchRepository.count();
    }

    /**
     * Get one typeCommune by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<TypeCommuneDTO> findOne(Long id) {
        log.debug("Request to get TypeCommune : {}", id);
        return typeCommuneRepository.findById(id).map(typeCommuneMapper::toDto);
    }

    /**
     * Delete the typeCommune by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TypeCommune : {}", id);
        return typeCommuneRepository.deleteById(id).then(typeCommuneSearchRepository.deleteById(id));
    }

    /**
     * Search for the typeCommune corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<TypeCommuneDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TypeCommunes for query {}", query);
        return typeCommuneSearchRepository.search(query, pageable).map(typeCommuneMapper::toDto);
    }
}
