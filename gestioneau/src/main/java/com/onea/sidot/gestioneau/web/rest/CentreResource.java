package com.onea.sidot.gestioneau.web.rest;

import com.onea.sidot.gestioneau.repository.CentreRepository;
import com.onea.sidot.gestioneau.service.CentreService;
import com.onea.sidot.gestioneau.service.dto.CentreDTO;
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
 * REST controller for managing {@link com.onea.sidot.gestioneau.domain.Centre}.
 */
@RestController
@RequestMapping("/api")
public class CentreResource {

    private final Logger log = LoggerFactory.getLogger(CentreResource.class);

    private static final String ENTITY_NAME = "gestioneauCentre";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CentreService centreService;

    private final CentreRepository centreRepository;

    public CentreResource(CentreService centreService, CentreRepository centreRepository) {
        this.centreService = centreService;
        this.centreRepository = centreRepository;
    }

    /**
     * {@code POST  /centres} : Create a new centre.
     *
     * @param centreDTO the centreDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new centreDTO, or with status {@code 400 (Bad Request)} if the centre has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/centres")
    public Mono<ResponseEntity<CentreDTO>> createCentre(@Valid @RequestBody CentreDTO centreDTO) throws URISyntaxException {
        log.debug("REST request to save Centre : {}", centreDTO);
        if (centreDTO.getId() != null) {
            throw new BadRequestAlertException("A new centre cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return centreService
            .save(centreDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/centres/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /centres/:id} : Updates an existing centre.
     *
     * @param id the id of the centreDTO to save.
     * @param centreDTO the centreDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated centreDTO,
     * or with status {@code 400 (Bad Request)} if the centreDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the centreDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/centres/{id}")
    public Mono<ResponseEntity<CentreDTO>> updateCentre(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CentreDTO centreDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Centre : {}, {}", id, centreDTO);
        if (centreDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, centreDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return centreRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return centreService
                        .save(centreDTO)
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
     * {@code PATCH  /centres/:id} : Partial updates given fields of an existing centre, field will ignore if it is null
     *
     * @param id the id of the centreDTO to save.
     * @param centreDTO the centreDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated centreDTO,
     * or with status {@code 400 (Bad Request)} if the centreDTO is not valid,
     * or with status {@code 404 (Not Found)} if the centreDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the centreDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/centres/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<CentreDTO>> partialUpdateCentre(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CentreDTO centreDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Centre partially : {}, {}", id, centreDTO);
        if (centreDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, centreDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return centreRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<CentreDTO> result = centreService.partialUpdate(centreDTO);

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
     * {@code GET  /centres} : get all the centres.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of centres in body.
     */
    @GetMapping("/centres")
    public Mono<ResponseEntity<List<CentreDTO>>> getAllCentres(
        Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false) String filter
    ) {
        if ("prevision-is-null".equals(filter)) {
            log.debug("REST request to get all Centres where prevision is null");
            return centreService.findAllWherePrevisionIsNull().collectList().map(ResponseEntity::ok);
        }
        log.debug("REST request to get a page of Centres");
        return centreService
            .countAll()
            .zipWith(centreService.findAll(pageable).collectList())
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
     * {@code GET  /centres/:id} : get the "id" centre.
     *
     * @param id the id of the centreDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the centreDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/centres/{id}")
    public Mono<ResponseEntity<CentreDTO>> getCentre(@PathVariable Long id) {
        log.debug("REST request to get Centre : {}", id);
        Mono<CentreDTO> centreDTO = centreService.findOne(id);
        return ResponseUtil.wrapOrNotFound(centreDTO);
    }

    /**
     * {@code DELETE  /centres/:id} : delete the "id" centre.
     *
     * @param id the id of the centreDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/centres/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCentre(@PathVariable Long id) {
        log.debug("REST request to delete Centre : {}", id);
        return centreService
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
     * {@code SEARCH  /_search/centres?query=:query} : search for the centre corresponding
     * to the query.
     *
     * @param query the query of the centre search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/centres")
    public Mono<ResponseEntity<Flux<CentreDTO>>> searchCentres(@RequestParam String query, Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to search for a page of Centres for query {}", query);
        return centreService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(centreService.search(query, pageable)));
    }
}
