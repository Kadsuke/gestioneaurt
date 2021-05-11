package com.onea.sidot.gestioneau.web.rest;

import com.onea.sidot.gestioneau.repository.DirectionRegionaleRepository;
import com.onea.sidot.gestioneau.service.DirectionRegionaleService;
import com.onea.sidot.gestioneau.service.dto.DirectionRegionaleDTO;
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
 * REST controller for managing {@link com.onea.sidot.gestioneau.domain.DirectionRegionale}.
 */
@RestController
@RequestMapping("/api")
public class DirectionRegionaleResource {

    private final Logger log = LoggerFactory.getLogger(DirectionRegionaleResource.class);

    private static final String ENTITY_NAME = "gestioneauDirectionRegionale";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DirectionRegionaleService directionRegionaleService;

    private final DirectionRegionaleRepository directionRegionaleRepository;

    public DirectionRegionaleResource(
        DirectionRegionaleService directionRegionaleService,
        DirectionRegionaleRepository directionRegionaleRepository
    ) {
        this.directionRegionaleService = directionRegionaleService;
        this.directionRegionaleRepository = directionRegionaleRepository;
    }

    /**
     * {@code POST  /direction-regionales} : Create a new directionRegionale.
     *
     * @param directionRegionaleDTO the directionRegionaleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new directionRegionaleDTO, or with status {@code 400 (Bad Request)} if the directionRegionale has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/direction-regionales")
    public Mono<ResponseEntity<DirectionRegionaleDTO>> createDirectionRegionale(
        @Valid @RequestBody DirectionRegionaleDTO directionRegionaleDTO
    ) throws URISyntaxException {
        log.debug("REST request to save DirectionRegionale : {}", directionRegionaleDTO);
        if (directionRegionaleDTO.getId() != null) {
            throw new BadRequestAlertException("A new directionRegionale cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return directionRegionaleService
            .save(directionRegionaleDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/direction-regionales/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /direction-regionales/:id} : Updates an existing directionRegionale.
     *
     * @param id the id of the directionRegionaleDTO to save.
     * @param directionRegionaleDTO the directionRegionaleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated directionRegionaleDTO,
     * or with status {@code 400 (Bad Request)} if the directionRegionaleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the directionRegionaleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/direction-regionales/{id}")
    public Mono<ResponseEntity<DirectionRegionaleDTO>> updateDirectionRegionale(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DirectionRegionaleDTO directionRegionaleDTO
    ) throws URISyntaxException {
        log.debug("REST request to update DirectionRegionale : {}, {}", id, directionRegionaleDTO);
        if (directionRegionaleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, directionRegionaleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return directionRegionaleRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return directionRegionaleService
                        .save(directionRegionaleDTO)
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
     * {@code PATCH  /direction-regionales/:id} : Partial updates given fields of an existing directionRegionale, field will ignore if it is null
     *
     * @param id the id of the directionRegionaleDTO to save.
     * @param directionRegionaleDTO the directionRegionaleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated directionRegionaleDTO,
     * or with status {@code 400 (Bad Request)} if the directionRegionaleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the directionRegionaleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the directionRegionaleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/direction-regionales/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<DirectionRegionaleDTO>> partialUpdateDirectionRegionale(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DirectionRegionaleDTO directionRegionaleDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update DirectionRegionale partially : {}, {}", id, directionRegionaleDTO);
        if (directionRegionaleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, directionRegionaleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return directionRegionaleRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<DirectionRegionaleDTO> result = directionRegionaleService.partialUpdate(directionRegionaleDTO);

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
     * {@code GET  /direction-regionales} : get all the directionRegionales.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of directionRegionales in body.
     */
    @GetMapping("/direction-regionales")
    public Mono<ResponseEntity<List<DirectionRegionaleDTO>>> getAllDirectionRegionales(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of DirectionRegionales");
        return directionRegionaleService
            .countAll()
            .zipWith(directionRegionaleService.findAll(pageable).collectList())
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
     * {@code GET  /direction-regionales/:id} : get the "id" directionRegionale.
     *
     * @param id the id of the directionRegionaleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the directionRegionaleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/direction-regionales/{id}")
    public Mono<ResponseEntity<DirectionRegionaleDTO>> getDirectionRegionale(@PathVariable Long id) {
        log.debug("REST request to get DirectionRegionale : {}", id);
        Mono<DirectionRegionaleDTO> directionRegionaleDTO = directionRegionaleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(directionRegionaleDTO);
    }

    /**
     * {@code DELETE  /direction-regionales/:id} : delete the "id" directionRegionale.
     *
     * @param id the id of the directionRegionaleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/direction-regionales/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteDirectionRegionale(@PathVariable Long id) {
        log.debug("REST request to delete DirectionRegionale : {}", id);
        return directionRegionaleService
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
     * {@code SEARCH  /_search/direction-regionales?query=:query} : search for the directionRegionale corresponding
     * to the query.
     *
     * @param query the query of the directionRegionale search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/direction-regionales")
    public Mono<ResponseEntity<Flux<DirectionRegionaleDTO>>> searchDirectionRegionales(
        @RequestParam String query,
        Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of DirectionRegionales for query {}", query);
        return directionRegionaleService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(directionRegionaleService.search(query, pageable)));
    }
}
