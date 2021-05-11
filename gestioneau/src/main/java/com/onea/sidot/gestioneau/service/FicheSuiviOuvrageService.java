package com.onea.sidot.gestioneau.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.onea.sidot.gestioneau.domain.FicheSuiviOuvrage;
import com.onea.sidot.gestioneau.repository.FicheSuiviOuvrageRepository;
import com.onea.sidot.gestioneau.repository.search.FicheSuiviOuvrageSearchRepository;
import com.onea.sidot.gestioneau.service.dto.FicheSuiviOuvrageDTO;
import com.onea.sidot.gestioneau.service.mapper.FicheSuiviOuvrageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link FicheSuiviOuvrage}.
 */
@Service
@Transactional
public class FicheSuiviOuvrageService {

    private final Logger log = LoggerFactory.getLogger(FicheSuiviOuvrageService.class);

    private final FicheSuiviOuvrageRepository ficheSuiviOuvrageRepository;

    private final FicheSuiviOuvrageMapper ficheSuiviOuvrageMapper;

    private final FicheSuiviOuvrageSearchRepository ficheSuiviOuvrageSearchRepository;

    public FicheSuiviOuvrageService(
        FicheSuiviOuvrageRepository ficheSuiviOuvrageRepository,
        FicheSuiviOuvrageMapper ficheSuiviOuvrageMapper,
        FicheSuiviOuvrageSearchRepository ficheSuiviOuvrageSearchRepository
    ) {
        this.ficheSuiviOuvrageRepository = ficheSuiviOuvrageRepository;
        this.ficheSuiviOuvrageMapper = ficheSuiviOuvrageMapper;
        this.ficheSuiviOuvrageSearchRepository = ficheSuiviOuvrageSearchRepository;
    }

    /**
     * Save a ficheSuiviOuvrage.
     *
     * @param ficheSuiviOuvrageDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<FicheSuiviOuvrageDTO> save(FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO) {
        log.debug("Request to save FicheSuiviOuvrage : {}", ficheSuiviOuvrageDTO);
        return ficheSuiviOuvrageRepository
            .save(ficheSuiviOuvrageMapper.toEntity(ficheSuiviOuvrageDTO))
            .flatMap(ficheSuiviOuvrageSearchRepository::save)
            .map(ficheSuiviOuvrageMapper::toDto);
    }

    /**
     * Partially update a ficheSuiviOuvrage.
     *
     * @param ficheSuiviOuvrageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<FicheSuiviOuvrageDTO> partialUpdate(FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO) {
        log.debug("Request to partially update FicheSuiviOuvrage : {}", ficheSuiviOuvrageDTO);

        return ficheSuiviOuvrageRepository
            .findById(ficheSuiviOuvrageDTO.getId())
            .map(
                existingFicheSuiviOuvrage -> {
                    ficheSuiviOuvrageMapper.partialUpdate(existingFicheSuiviOuvrage, ficheSuiviOuvrageDTO);
                    return existingFicheSuiviOuvrage;
                }
            )
            .flatMap(ficheSuiviOuvrageRepository::save)
            .flatMap(
                savedFicheSuiviOuvrage -> {
                    ficheSuiviOuvrageSearchRepository.save(savedFicheSuiviOuvrage);

                    return Mono.just(savedFicheSuiviOuvrage);
                }
            )
            .map(ficheSuiviOuvrageMapper::toDto);
    }

    /**
     * Get all the ficheSuiviOuvrages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<FicheSuiviOuvrageDTO> findAll(Pageable pageable) {
        log.debug("Request to get all FicheSuiviOuvrages");
        return ficheSuiviOuvrageRepository.findAllBy(pageable).map(ficheSuiviOuvrageMapper::toDto);
    }

    /**
     * Returns the number of ficheSuiviOuvrages available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return ficheSuiviOuvrageRepository.count();
    }

    /**
     * Returns the number of ficheSuiviOuvrages available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return ficheSuiviOuvrageSearchRepository.count();
    }

    /**
     * Get one ficheSuiviOuvrage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<FicheSuiviOuvrageDTO> findOne(Long id) {
        log.debug("Request to get FicheSuiviOuvrage : {}", id);
        return ficheSuiviOuvrageRepository.findById(id).map(ficheSuiviOuvrageMapper::toDto);
    }

    /**
     * Delete the ficheSuiviOuvrage by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete FicheSuiviOuvrage : {}", id);
        return ficheSuiviOuvrageRepository.deleteById(id).then(ficheSuiviOuvrageSearchRepository.deleteById(id));
    }

    /**
     * Search for the ficheSuiviOuvrage corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<FicheSuiviOuvrageDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of FicheSuiviOuvrages for query {}", query);
        return ficheSuiviOuvrageSearchRepository.search(query, pageable).map(ficheSuiviOuvrageMapper::toDto);
    }
}
