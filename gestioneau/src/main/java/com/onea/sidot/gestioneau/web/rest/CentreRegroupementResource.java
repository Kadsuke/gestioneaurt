package com.onea.sidot.gestioneau.web.rest;

import com.onea.sidot.gestioneau.repository.CentreRegroupementRepository;
import com.onea.sidot.gestioneau.service.CentreRegroupementService;
import com.onea.sidot.gestioneau.service.dto.CentreRegroupementDTO;
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
 * REST controller for managing {@link com.onea.sidot.gestioneau.domain.CentreRegroupement}.
 */
@RestController
@RequestMapping("/api")
public class CentreRegroupementResource {

    private final Logger log = LoggerFactory.getLogger(CentreRegroupementResource.class);

    private static final String ENTITY_NAME = "gestioneauCentreRegroupement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CentreRegroupementService centreRegroupementService;

    private final CentreRegroupementRepository centreRegroupementRepository;

    public CentreRegroupementResource(
        CentreRegroupementService centreRegroupementService,
        CentreRegroupementRepository centreRegroupementRepository
    ) {
        this.centreRegroupementService = centreRegroupementService;
        this.centreRegroupementRepository = centreRegroupementRepository;
    }

    /**
     * {@code POST  /centre-regroupements} : Create a new centreRegroupement.
     *
     * @param centreRegroupementDTO the centreRegroupementDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new centreRegroupementDTO, or with status {@code 400 (Bad Request)} if the centreRegroupement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/centre-regroupements")
    public Mono<ResponseEntity<CentreRegroupementDTO>> createCentreRegroupement(
        @Valid @RequestBody CentreRegroupementDTO centreRegroupementDTO
    ) throws URISyntaxException {
        log.debug("REST request to save CentreRegroupement : {}", centreRegroupementDTO);
        if (centreRegroupementDTO.getId() != null) {
            throw new BadRequestAlertException("A new centreRegroupement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return centreRegroupementService
            .save(centreRegroupementDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/centre-regroupements/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /centre-regroupements/:id} : Updates an existing centreRegroupement.
     *
     * @param id the id of the centreRegroupementDTO to save.
     * @param centreRegroupementDTO the centreRegroupementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated centreRegroupementDTO,
     * or with status {@code 400 (Bad Request)} if the centreRegroupementDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the centreRegroupementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/centre-regroupements/{id}")
    public Mono<ResponseEntity<CentreRegroupementDTO>> updateCentreRegroupement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CentreRegroupementDTO centreRegroupementDTO
    ) throws URISyntaxException {
        log.debug("REST request to update CentreRegroupement : {}, {}", id, centreRegroupementDTO);
        if (centreRegroupementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, centreRegroupementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return centreRegroupementRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return centreRegroupementService
                        .save(centreRegroupementDTO)
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
     * {@code PATCH  /centre-regroupements/:id} : Partial updates given fields of an existing centreRegroupement, field will ignore if it is null
     *
     * @param id the id of the centreRegroupementDTO to save.
     * @param centreRegroupementDTO the centreRegroupementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated centreRegroupementDTO,
     * or with status {@code 400 (Bad Request)} if the centreRegroupementDTO is not valid,
     * or with status {@code 404 (Not Found)} if the centreRegroupementDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the centreRegroupementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/centre-regroupements/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<CentreRegroupementDTO>> partialUpdateCentreRegroupement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CentreRegroupementDTO centreRegroupementDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update CentreRegroupement partially : {}, {}", id, centreRegroupementDTO);
        if (centreRegroupementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, centreRegroupementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return centreRegroupementRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<CentreRegroupementDTO> result = centreRegroupementService.partialUpdate(centreRegroupementDTO);

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
     * {@code GET  /centre-regroupements} : get all the centreRegroupements.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of centreRegroupements in body.
     */
    @GetMapping("/centre-regroupements")
    public Mono<ResponseEntity<List<CentreRegroupementDTO>>> getAllCentreRegroupements(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of CentreRegroupements");
        return centreRegroupementService
            .countAll()
            .zipWith(centreRegroupementService.findAll(pageable).collectList())
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
     * {@code GET  /centre-regroupements/:id} : get the "id" centreRegroupement.
     *
     * @param id the id of the centreRegroupementDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the centreRegroupementDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/centre-regroupements/{id}")
    public Mono<ResponseEntity<CentreRegroupementDTO>> getCentreRegroupement(@PathVariable Long id) {
        log.debug("REST request to get CentreRegroupement : {}", id);
        Mono<CentreRegroupementDTO> centreRegroupementDTO = centreRegroupementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(centreRegroupementDTO);
    }

    /**
     * {@code DELETE  /centre-regroupements/:id} : delete the "id" centreRegroupement.
     *
     * @param id the id of the centreRegroupementDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/centre-regroupements/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCentreRegroupement(@PathVariable Long id) {
        log.debug("REST request to delete CentreRegroupement : {}", id);
        return centreRegroupementService
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
     * {@code SEARCH  /_search/centre-regroupements?query=:query} : search for the centreRegroupement corresponding
     * to the query.
     *
     * @param query the query of the centreRegroupement search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/centre-regroupements")
    public Mono<ResponseEntity<Flux<CentreRegroupementDTO>>> searchCentreRegroupements(
        @RequestParam String query,
        Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of CentreRegroupements for query {}", query);
        return centreRegroupementService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(centreRegroupementService.search(query, pageable)));
    }
}
