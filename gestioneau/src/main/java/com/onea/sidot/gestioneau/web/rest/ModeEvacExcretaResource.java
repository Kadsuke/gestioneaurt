package com.onea.sidot.gestioneau.web.rest;

import com.onea.sidot.gestioneau.repository.ModeEvacExcretaRepository;
import com.onea.sidot.gestioneau.service.ModeEvacExcretaService;
import com.onea.sidot.gestioneau.service.dto.ModeEvacExcretaDTO;
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
 * REST controller for managing {@link com.onea.sidot.gestioneau.domain.ModeEvacExcreta}.
 */
@RestController
@RequestMapping("/api")
public class ModeEvacExcretaResource {

    private final Logger log = LoggerFactory.getLogger(ModeEvacExcretaResource.class);

    private static final String ENTITY_NAME = "gestioneauModeEvacExcreta";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ModeEvacExcretaService modeEvacExcretaService;

    private final ModeEvacExcretaRepository modeEvacExcretaRepository;

    public ModeEvacExcretaResource(ModeEvacExcretaService modeEvacExcretaService, ModeEvacExcretaRepository modeEvacExcretaRepository) {
        this.modeEvacExcretaService = modeEvacExcretaService;
        this.modeEvacExcretaRepository = modeEvacExcretaRepository;
    }

    /**
     * {@code POST  /mode-evac-excretas} : Create a new modeEvacExcreta.
     *
     * @param modeEvacExcretaDTO the modeEvacExcretaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new modeEvacExcretaDTO, or with status {@code 400 (Bad Request)} if the modeEvacExcreta has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/mode-evac-excretas")
    public Mono<ResponseEntity<ModeEvacExcretaDTO>> createModeEvacExcreta(@Valid @RequestBody ModeEvacExcretaDTO modeEvacExcretaDTO)
        throws URISyntaxException {
        log.debug("REST request to save ModeEvacExcreta : {}", modeEvacExcretaDTO);
        if (modeEvacExcretaDTO.getId() != null) {
            throw new BadRequestAlertException("A new modeEvacExcreta cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return modeEvacExcretaService
            .save(modeEvacExcretaDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/mode-evac-excretas/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /mode-evac-excretas/:id} : Updates an existing modeEvacExcreta.
     *
     * @param id the id of the modeEvacExcretaDTO to save.
     * @param modeEvacExcretaDTO the modeEvacExcretaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated modeEvacExcretaDTO,
     * or with status {@code 400 (Bad Request)} if the modeEvacExcretaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the modeEvacExcretaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/mode-evac-excretas/{id}")
    public Mono<ResponseEntity<ModeEvacExcretaDTO>> updateModeEvacExcreta(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ModeEvacExcretaDTO modeEvacExcretaDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ModeEvacExcreta : {}, {}", id, modeEvacExcretaDTO);
        if (modeEvacExcretaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, modeEvacExcretaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return modeEvacExcretaRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return modeEvacExcretaService
                        .save(modeEvacExcretaDTO)
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
     * {@code PATCH  /mode-evac-excretas/:id} : Partial updates given fields of an existing modeEvacExcreta, field will ignore if it is null
     *
     * @param id the id of the modeEvacExcretaDTO to save.
     * @param modeEvacExcretaDTO the modeEvacExcretaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated modeEvacExcretaDTO,
     * or with status {@code 400 (Bad Request)} if the modeEvacExcretaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the modeEvacExcretaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the modeEvacExcretaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/mode-evac-excretas/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<ModeEvacExcretaDTO>> partialUpdateModeEvacExcreta(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ModeEvacExcretaDTO modeEvacExcretaDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ModeEvacExcreta partially : {}, {}", id, modeEvacExcretaDTO);
        if (modeEvacExcretaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, modeEvacExcretaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return modeEvacExcretaRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<ModeEvacExcretaDTO> result = modeEvacExcretaService.partialUpdate(modeEvacExcretaDTO);

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
     * {@code GET  /mode-evac-excretas} : get all the modeEvacExcretas.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of modeEvacExcretas in body.
     */
    @GetMapping("/mode-evac-excretas")
    public Mono<ResponseEntity<List<ModeEvacExcretaDTO>>> getAllModeEvacExcretas(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of ModeEvacExcretas");
        return modeEvacExcretaService
            .countAll()
            .zipWith(modeEvacExcretaService.findAll(pageable).collectList())
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
     * {@code GET  /mode-evac-excretas/:id} : get the "id" modeEvacExcreta.
     *
     * @param id the id of the modeEvacExcretaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the modeEvacExcretaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/mode-evac-excretas/{id}")
    public Mono<ResponseEntity<ModeEvacExcretaDTO>> getModeEvacExcreta(@PathVariable Long id) {
        log.debug("REST request to get ModeEvacExcreta : {}", id);
        Mono<ModeEvacExcretaDTO> modeEvacExcretaDTO = modeEvacExcretaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(modeEvacExcretaDTO);
    }

    /**
     * {@code DELETE  /mode-evac-excretas/:id} : delete the "id" modeEvacExcreta.
     *
     * @param id the id of the modeEvacExcretaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/mode-evac-excretas/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteModeEvacExcreta(@PathVariable Long id) {
        log.debug("REST request to delete ModeEvacExcreta : {}", id);
        return modeEvacExcretaService
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
     * {@code SEARCH  /_search/mode-evac-excretas?query=:query} : search for the modeEvacExcreta corresponding
     * to the query.
     *
     * @param query the query of the modeEvacExcreta search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/mode-evac-excretas")
    public Mono<ResponseEntity<Flux<ModeEvacExcretaDTO>>> searchModeEvacExcretas(
        @RequestParam String query,
        Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of ModeEvacExcretas for query {}", query);
        return modeEvacExcretaService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(modeEvacExcretaService.search(query, pageable)));
    }
}
