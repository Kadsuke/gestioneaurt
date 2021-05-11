package com.onea.sidot.gestioneau.web.rest;

import com.onea.sidot.gestioneau.repository.NatureOuvrageRepository;
import com.onea.sidot.gestioneau.service.NatureOuvrageService;
import com.onea.sidot.gestioneau.service.dto.NatureOuvrageDTO;
import com.onea.sidot.gestioneau.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.onea.sidot.gestioneau.domain.NatureOuvrage}.
 */
@RestController
@RequestMapping("/api")
public class NatureOuvrageResource {

    private final Logger log = LoggerFactory.getLogger(NatureOuvrageResource.class);

    private static final String ENTITY_NAME = "gestioneauNatureOuvrage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NatureOuvrageService natureOuvrageService;

    private final NatureOuvrageRepository natureOuvrageRepository;

    public NatureOuvrageResource(NatureOuvrageService natureOuvrageService, NatureOuvrageRepository natureOuvrageRepository) {
        this.natureOuvrageService = natureOuvrageService;
        this.natureOuvrageRepository = natureOuvrageRepository;
    }

    /**
     * {@code POST  /nature-ouvrages} : Create a new natureOuvrage.
     *
     * @param natureOuvrageDTO the natureOuvrageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new natureOuvrageDTO, or with status {@code 400 (Bad Request)} if the natureOuvrage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/nature-ouvrages")
    public Mono<ResponseEntity<NatureOuvrageDTO>> createNatureOuvrage(@Valid @RequestBody NatureOuvrageDTO natureOuvrageDTO)
        throws URISyntaxException {
        log.debug("REST request to save NatureOuvrage : {}", natureOuvrageDTO);
        if (natureOuvrageDTO.getId() != null) {
            throw new BadRequestAlertException("A new natureOuvrage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return natureOuvrageService
            .save(natureOuvrageDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/nature-ouvrages/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /nature-ouvrages/:id} : Updates an existing natureOuvrage.
     *
     * @param id the id of the natureOuvrageDTO to save.
     * @param natureOuvrageDTO the natureOuvrageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated natureOuvrageDTO,
     * or with status {@code 400 (Bad Request)} if the natureOuvrageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the natureOuvrageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/nature-ouvrages/{id}")
    public Mono<ResponseEntity<NatureOuvrageDTO>> updateNatureOuvrage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NatureOuvrageDTO natureOuvrageDTO
    ) throws URISyntaxException {
        log.debug("REST request to update NatureOuvrage : {}, {}", id, natureOuvrageDTO);
        if (natureOuvrageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, natureOuvrageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return natureOuvrageRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return natureOuvrageService
                        .save(natureOuvrageDTO)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            result ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString())
                                    )
                                    .body(result)
                        );
                }
            );
    }

    /**
     * {@code PATCH  /nature-ouvrages/:id} : Partial updates given fields of an existing natureOuvrage, field will ignore if it is null
     *
     * @param id the id of the natureOuvrageDTO to save.
     * @param natureOuvrageDTO the natureOuvrageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated natureOuvrageDTO,
     * or with status {@code 400 (Bad Request)} if the natureOuvrageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the natureOuvrageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the natureOuvrageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/nature-ouvrages/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<NatureOuvrageDTO>> partialUpdateNatureOuvrage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NatureOuvrageDTO natureOuvrageDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update NatureOuvrage partially : {}, {}", id, natureOuvrageDTO);
        if (natureOuvrageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, natureOuvrageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return natureOuvrageRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<NatureOuvrageDTO> result = natureOuvrageService.partialUpdate(natureOuvrageDTO);

                    return result
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            res ->
                                ResponseEntity
                                    .ok()
                                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                                    .body(res)
                        );
                }
            );
    }

    /**
     * {@code GET  /nature-ouvrages} : get all the natureOuvrages.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of natureOuvrages in body.
     */
    @GetMapping("/nature-ouvrages")
    public Mono<ResponseEntity<List<NatureOuvrageDTO>>> getAllNatureOuvrages(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of NatureOuvrages");
        return natureOuvrageService
            .countAll()
            .zipWith(natureOuvrageService.findAll(pageable).collectList())
            .map(
                countWithEntities -> {
                    return ResponseEntity
                        .ok()
                        .headers(
                            PaginationUtil.generatePaginationHttpHeaders(
                                UriComponentsBuilder.fromHttpRequest(request),
                                new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                            )
                        )
                        .body(countWithEntities.getT2());
                }
            );
    }

    /**
     * {@code GET  /nature-ouvrages/:id} : get the "id" natureOuvrage.
     *
     * @param id the id of the natureOuvrageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the natureOuvrageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/nature-ouvrages/{id}")
    public Mono<ResponseEntity<NatureOuvrageDTO>> getNatureOuvrage(@PathVariable Long id) {
        log.debug("REST request to get NatureOuvrage : {}", id);
        Mono<NatureOuvrageDTO> natureOuvrageDTO = natureOuvrageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(natureOuvrageDTO);
    }

    /**
     * {@code DELETE  /nature-ouvrages/:id} : delete the "id" natureOuvrage.
     *
     * @param id the id of the natureOuvrageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/nature-ouvrages/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteNatureOuvrage(@PathVariable Long id) {
        log.debug("REST request to delete NatureOuvrage : {}", id);
        return natureOuvrageService
            .delete(id)
            .map(
                result ->
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
            );
    }

    /**
     * {@code SEARCH  /_search/nature-ouvrages?query=:query} : search for the natureOuvrage corresponding
     * to the query.
     *
     * @param query the query of the natureOuvrage search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/nature-ouvrages")
    public Mono<ResponseEntity<Flux<NatureOuvrageDTO>>> searchNatureOuvrages(
        @RequestParam String query,
        Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of NatureOuvrages for query {}", query);
        return natureOuvrageService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(natureOuvrageService.search(query, pageable)));
    }
}
