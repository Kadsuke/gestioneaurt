package com.onea.sidot.gestioneau.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.onea.sidot.gestioneau.domain.Macon;
import com.onea.sidot.gestioneau.repository.MaconRepository;
import com.onea.sidot.gestioneau.repository.search.MaconSearchRepository;
import com.onea.sidot.gestioneau.service.dto.MaconDTO;
import com.onea.sidot.gestioneau.service.mapper.MaconMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Macon}.
 */
@Service
@Transactional
public class MaconService {

    private final Logger log = LoggerFactory.getLogger(MaconService.class);

    private final MaconRepository maconRepository;

    private final MaconMapper maconMapper;

    private final MaconSearchRepository maconSearchRepository;

    public MaconService(MaconRepository maconRepository, MaconMapper maconMapper, MaconSearchRepository maconSearchRepository) {
        this.maconRepository = maconRepository;
        this.maconMapper = maconMapper;
        this.maconSearchRepository = maconSearchRepository;
    }

    /**
     * Save a macon.
     *
     * @param maconDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<MaconDTO> save(MaconDTO maconDTO) {
        log.debug("Request to save Macon : {}", maconDTO);
        return maconRepository.save(maconMapper.toEntity(maconDTO)).flatMap(maconSearchRepository::save).map(maconMapper::toDto);
    }

    /**
     * Partially update a macon.
     *
     * @param maconDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<MaconDTO> partialUpdate(MaconDTO maconDTO) {
        log.debug("Request to partially update Macon : {}", maconDTO);

        return maconRepository
            .findById(maconDTO.getId())
            .map(
                existingMacon -> {
                    maconMapper.partialUpdate(existingMacon, maconDTO);
                    return existingMacon;
                }
            )
            .flatMap(maconRepository::save)
            .flatMap(
                savedMacon -> {
                    maconSearchRepository.save(savedMacon);

                    return Mono.just(savedMacon);
                }
            )
            .map(maconMapper::toDto);
    }

    /**
     * Get all the macons.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<MaconDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Macons");
        return maconRepository.findAllBy(pageable).map(maconMapper::toDto);
    }

    /**
     * Returns the number of macons available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return maconRepository.count();
    }

    /**
     * Returns the number of macons available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return maconSearchRepository.count();
    }

    /**
     * Get one macon by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<MaconDTO> findOne(Long id) {
        log.debug("Request to get Macon : {}", id);
        return maconRepository.findById(id).map(maconMapper::toDto);
    }

    /**
     * Delete the macon by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Macon : {}", id);
        return maconRepository.deleteById(id).then(maconSearchRepository.deleteById(id));
    }

    /**
     * Search for the macon corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<MaconDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Macons for query {}", query);
        return maconSearchRepository.search(query, pageable).map(maconMapper::toDto);
    }
}
