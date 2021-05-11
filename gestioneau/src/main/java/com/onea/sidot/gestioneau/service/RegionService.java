package com.onea.sidot.gestioneau.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.onea.sidot.gestioneau.domain.Region;
import com.onea.sidot.gestioneau.repository.RegionRepository;
import com.onea.sidot.gestioneau.repository.search.RegionSearchRepository;
import com.onea.sidot.gestioneau.service.dto.RegionDTO;
import com.onea.sidot.gestioneau.service.mapper.RegionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Region}.
 */
@Service
@Transactional
public class RegionService {

    private final Logger log = LoggerFactory.getLogger(RegionService.class);

    private final RegionRepository regionRepository;

    private final RegionMapper regionMapper;

    private final RegionSearchRepository regionSearchRepository;

    public RegionService(RegionRepository regionRepository, RegionMapper regionMapper, RegionSearchRepository regionSearchRepository) {
        this.regionRepository = regionRepository;
        this.regionMapper = regionMapper;
        this.regionSearchRepository = regionSearchRepository;
    }

    /**
     * Save a region.
     *
     * @param regionDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RegionDTO> save(RegionDTO regionDTO) {
        log.debug("Request to save Region : {}", regionDTO);
        return regionRepository.save(regionMapper.toEntity(regionDTO)).flatMap(regionSearchRepository::save).map(regionMapper::toDto);
    }

    /**
     * Partially update a region.
     *
     * @param regionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<RegionDTO> partialUpdate(RegionDTO regionDTO) {
        log.debug("Request to partially update Region : {}", regionDTO);

        return regionRepository
            .findById(regionDTO.getId())
            .map(
                existingRegion -> {
                    regionMapper.partialUpdate(existingRegion, regionDTO);
                    return existingRegion;
                }
            )
            .flatMap(regionRepository::save)
            .flatMap(
                savedRegion -> {
                    regionSearchRepository.save(savedRegion);

                    return Mono.just(savedRegion);
                }
            )
            .map(regionMapper::toDto);
    }

    /**
     * Get all the regions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RegionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Regions");
        return regionRepository.findAllBy(pageable).map(regionMapper::toDto);
    }

    /**
     * Returns the number of regions available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return regionRepository.count();
    }

    /**
     * Returns the number of regions available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return regionSearchRepository.count();
    }

    /**
     * Get one region by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<RegionDTO> findOne(Long id) {
        log.debug("Request to get Region : {}", id);
        return regionRepository.findById(id).map(regionMapper::toDto);
    }

    /**
     * Delete the region by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Region : {}", id);
        return regionRepository.deleteById(id).then(regionSearchRepository.deleteById(id));
    }

    /**
     * Search for the region corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RegionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Regions for query {}", query);
        return regionSearchRepository.search(query, pageable).map(regionMapper::toDto);
    }
}
