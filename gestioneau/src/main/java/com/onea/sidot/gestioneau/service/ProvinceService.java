package com.onea.sidot.gestioneau.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.onea.sidot.gestioneau.domain.Province;
import com.onea.sidot.gestioneau.repository.ProvinceRepository;
import com.onea.sidot.gestioneau.repository.search.ProvinceSearchRepository;
import com.onea.sidot.gestioneau.service.dto.ProvinceDTO;
import com.onea.sidot.gestioneau.service.mapper.ProvinceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Province}.
 */
@Service
@Transactional
public class ProvinceService {

    private final Logger log = LoggerFactory.getLogger(ProvinceService.class);

    private final ProvinceRepository provinceRepository;

    private final ProvinceMapper provinceMapper;

    private final ProvinceSearchRepository provinceSearchRepository;

    public ProvinceService(
        ProvinceRepository provinceRepository,
        ProvinceMapper provinceMapper,
        ProvinceSearchRepository provinceSearchRepository
    ) {
        this.provinceRepository = provinceRepository;
        this.provinceMapper = provinceMapper;
        this.provinceSearchRepository = provinceSearchRepository;
    }

    /**
     * Save a province.
     *
     * @param provinceDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProvinceDTO> save(ProvinceDTO provinceDTO) {
        log.debug("Request to save Province : {}", provinceDTO);
        return provinceRepository
            .save(provinceMapper.toEntity(provinceDTO))
            .flatMap(provinceSearchRepository::save)
            .map(provinceMapper::toDto);
    }

    /**
     * Partially update a province.
     *
     * @param provinceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProvinceDTO> partialUpdate(ProvinceDTO provinceDTO) {
        log.debug("Request to partially update Province : {}", provinceDTO);

        return provinceRepository
            .findById(provinceDTO.getId())
            .map(
                existingProvince -> {
                    provinceMapper.partialUpdate(existingProvince, provinceDTO);
                    return existingProvince;
                }
            )
            .flatMap(provinceRepository::save)
            .flatMap(
                savedProvince -> {
                    provinceSearchRepository.save(savedProvince);

                    return Mono.just(savedProvince);
                }
            )
            .map(provinceMapper::toDto);
    }

    /**
     * Get all the provinces.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProvinceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Provinces");
        return provinceRepository.findAllBy(pageable).map(provinceMapper::toDto);
    }

    /**
     * Returns the number of provinces available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return provinceRepository.count();
    }

    /**
     * Returns the number of provinces available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return provinceSearchRepository.count();
    }

    /**
     * Get one province by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProvinceDTO> findOne(Long id) {
        log.debug("Request to get Province : {}", id);
        return provinceRepository.findById(id).map(provinceMapper::toDto);
    }

    /**
     * Delete the province by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Province : {}", id);
        return provinceRepository.deleteById(id).then(provinceSearchRepository.deleteById(id));
    }

    /**
     * Search for the province corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProvinceDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Provinces for query {}", query);
        return provinceSearchRepository.search(query, pageable).map(provinceMapper::toDto);
    }
}
