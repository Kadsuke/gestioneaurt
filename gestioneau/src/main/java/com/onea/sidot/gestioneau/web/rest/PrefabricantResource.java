package com.onea.sidot.gestioneau.web.rest;

import com.onea.sidot.gestioneau.repository.PrefabricantRepository;
import com.onea.sidot.gestioneau.service.PrefabricantService;
import com.onea.sidot.gestioneau.service.dto.PrefabricantDTO;
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
 * REST controller for managing {@link com.onea.sidot.gestioneau.domain.Prefabricant}.
 */
@RestController
@RequestMapping("/api")
public class PrefabricantResource {

    private final Logger log = LoggerFactory.getLogger(PrefabricantResource.class);

    private static final String ENTITY_NAME = "gestioneauPrefabricant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PrefabricantService prefabricantService;

    private final PrefabricantRepository prefabricantRepository;

    public PrefabricantResource(PrefabricantService prefabricantService, PrefabricantRepository prefabricantRepository) {
        this.prefabricantService = prefabricantService;
        this.prefabricantRepository = prefabricantRepository;
    }

    /**
     * {@code POST  /prefabricants} : Create a new prefabricant.
     *
     * @param prefabricantDTO the prefabricantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new prefabricantDTO, or with status {@code 400 (Bad Request)} if the prefabricant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/prefabricants")
    public Mono<ResponseEntity<PrefabricantDTO>> createPrefabricant(@Valid @RequestBody PrefabricantDTO prefabricantDTO)
        throws URISyntaxException {
        log.debug("REST request to save Prefabricant : {}", prefabricantDTO);
        if (prefabricantDTO.getId() != null) {
            throw new BadRequestAlertException("A new prefabricant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return prefabricantService
            .save(prefabricantDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/prefabricants/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /prefabricants/:id} : Updates an existing prefabricant.
     *
     * @param id the id of the prefabricantDTO to save.
     * @param prefabricantDTO the prefabricantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated prefabricantDTO,
     * or with status {@code 400 (Bad Request)} if the prefabricantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the prefabricantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/prefabricants/{id}")
    public Mono<ResponseEntity<PrefabricantDTO>> updatePrefabricant(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PrefabricantDTO prefabricantDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Prefabricant : {}, {}", id, prefabricantDTO);
        if (prefabricantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, prefabricantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return prefabricantRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return prefabricantService
                        .save(prefabricantDTO)
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
     * {@code PATCH  /prefabricants/:id} : Partial updates given fields of an existing prefabricant, field will ignore if it is null
     *
     * @param id the id of the prefabricantDTO to save.
     * @param prefabricantDTO the prefabricantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated prefabricantDTO,
     * or with status {@code 400 (Bad Request)} if the prefabricantDTO is not valid,
     * or with status {@code 404 (Not Found)} if the prefabricantDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the prefabricantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/prefabricants/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<PrefabricantDTO>> partialUpdatePrefabricant(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PrefabricantDTO prefabricantDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Prefabricant partially : {}, {}", id, prefabricantDTO);
        if (prefabricantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, prefabricantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return prefabricantRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<PrefabricantDTO> result = prefabricantService.partialUpdate(prefabricantDTO);

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
     * {@code GET  /prefabricants} : get all the prefabricants.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of prefabricants in body.
     */
    @GetMapping("/prefabricants")
    public Mono<ResponseEntity<List<PrefabricantDTO>>> getAllPrefabricants(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Prefabricants");
        return prefabricantService
            .countAll()
            .zipWith(prefabricantService.findAll(pageable).collectList())
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
     * {@code GET  /prefabricants/:id} : get the "id" prefabricant.
     *
     * @param id the id of the prefabricantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the prefabricantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/prefabricants/{id}")
    public Mono<ResponseEntity<PrefabricantDTO>> getPrefabricant(@PathVariable Long id) {
        log.debug("REST request to get Prefabricant : {}", id);
        Mono<PrefabricantDTO> prefabricantDTO = prefabricantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(prefabricantDTO);
    }

    /**
     * {@code DELETE  /prefabricants/:id} : delete the "id" prefabricant.
     *
     * @param id the id of the prefabricantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/prefabricants/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePrefabricant(@PathVariable Long id) {
        log.debug("REST request to delete Prefabricant : {}", id);
        return prefabricantService
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
     * {@code SEARCH  /_search/prefabricants?query=:query} : search for the prefabricant corresponding
     * to the query.
     *
     * @param query the query of the prefabricant search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/prefabricants")
    public Mono<ResponseEntity<Flux<PrefabricantDTO>>> searchPrefabricants(
        @RequestParam String query,
        Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Prefabricants for query {}", query);
        return prefabricantService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(prefabricantService.search(query, pageable)));
    }
}
