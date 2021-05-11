package com.onea.sidot.gestioneau.web.rest;

import com.onea.sidot.gestioneau.repository.ModeEvacuationEauUseeRepository;
import com.onea.sidot.gestioneau.service.ModeEvacuationEauUseeService;
import com.onea.sidot.gestioneau.service.dto.ModeEvacuationEauUseeDTO;
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
 * REST controller for managing {@link com.onea.sidot.gestioneau.domain.ModeEvacuationEauUsee}.
 */
@RestController
@RequestMapping("/api")
public class ModeEvacuationEauUseeResource {

    private final Logger log = LoggerFactory.getLogger(ModeEvacuationEauUseeResource.class);

    private static final String ENTITY_NAME = "gestioneauModeEvacuationEauUsee";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ModeEvacuationEauUseeService modeEvacuationEauUseeService;

    private final ModeEvacuationEauUseeRepository modeEvacuationEauUseeRepository;

    public ModeEvacuationEauUseeResource(
        ModeEvacuationEauUseeService modeEvacuationEauUseeService,
        ModeEvacuationEauUseeRepository modeEvacuationEauUseeRepository
    ) {
        this.modeEvacuationEauUseeService = modeEvacuationEauUseeService;
        this.modeEvacuationEauUseeRepository = modeEvacuationEauUseeRepository;
    }

    /**
     * {@code POST  /mode-evacuation-eau-usees} : Create a new modeEvacuationEauUsee.
     *
     * @param modeEvacuationEauUseeDTO the modeEvacuationEauUseeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new modeEvacuationEauUseeDTO, or with status {@code 400 (Bad Request)} if the modeEvacuationEauUsee has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/mode-evacuation-eau-usees")
    public Mono<ResponseEntity<ModeEvacuationEauUseeDTO>> createModeEvacuationEauUsee(
        @Valid @RequestBody ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO
    ) throws URISyntaxException {
        log.debug("REST request to save ModeEvacuationEauUsee : {}", modeEvacuationEauUseeDTO);
        if (modeEvacuationEauUseeDTO.getId() != null) {
            throw new BadRequestAlertException("A new modeEvacuationEauUsee cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return modeEvacuationEauUseeService
            .save(modeEvacuationEauUseeDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/mode-evacuation-eau-usees/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /mode-evacuation-eau-usees/:id} : Updates an existing modeEvacuationEauUsee.
     *
     * @param id the id of the modeEvacuationEauUseeDTO to save.
     * @param modeEvacuationEauUseeDTO the modeEvacuationEauUseeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated modeEvacuationEauUseeDTO,
     * or with status {@code 400 (Bad Request)} if the modeEvacuationEauUseeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the modeEvacuationEauUseeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/mode-evacuation-eau-usees/{id}")
    public Mono<ResponseEntity<ModeEvacuationEauUseeDTO>> updateModeEvacuationEauUsee(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ModeEvacuationEauUsee : {}, {}", id, modeEvacuationEauUseeDTO);
        if (modeEvacuationEauUseeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, modeEvacuationEauUseeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return modeEvacuationEauUseeRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return modeEvacuationEauUseeService
                        .save(modeEvacuationEauUseeDTO)
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
     * {@code PATCH  /mode-evacuation-eau-usees/:id} : Partial updates given fields of an existing modeEvacuationEauUsee, field will ignore if it is null
     *
     * @param id the id of the modeEvacuationEauUseeDTO to save.
     * @param modeEvacuationEauUseeDTO the modeEvacuationEauUseeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated modeEvacuationEauUseeDTO,
     * or with status {@code 400 (Bad Request)} if the modeEvacuationEauUseeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the modeEvacuationEauUseeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the modeEvacuationEauUseeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/mode-evacuation-eau-usees/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<ModeEvacuationEauUseeDTO>> partialUpdateModeEvacuationEauUsee(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ModeEvacuationEauUsee partially : {}, {}", id, modeEvacuationEauUseeDTO);
        if (modeEvacuationEauUseeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, modeEvacuationEauUseeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return modeEvacuationEauUseeRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<ModeEvacuationEauUseeDTO> result = modeEvacuationEauUseeService.partialUpdate(modeEvacuationEauUseeDTO);

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
     * {@code GET  /mode-evacuation-eau-usees} : get all the modeEvacuationEauUsees.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of modeEvacuationEauUsees in body.
     */
    @GetMapping("/mode-evacuation-eau-usees")
    public Mono<ResponseEntity<List<ModeEvacuationEauUseeDTO>>> getAllModeEvacuationEauUsees(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of ModeEvacuationEauUsees");
        return modeEvacuationEauUseeService
            .countAll()
            .zipWith(modeEvacuationEauUseeService.findAll(pageable).collectList())
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
     * {@code GET  /mode-evacuation-eau-usees/:id} : get the "id" modeEvacuationEauUsee.
     *
     * @param id the id of the modeEvacuationEauUseeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the modeEvacuationEauUseeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/mode-evacuation-eau-usees/{id}")
    public Mono<ResponseEntity<ModeEvacuationEauUseeDTO>> getModeEvacuationEauUsee(@PathVariable Long id) {
        log.debug("REST request to get ModeEvacuationEauUsee : {}", id);
        Mono<ModeEvacuationEauUseeDTO> modeEvacuationEauUseeDTO = modeEvacuationEauUseeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(modeEvacuationEauUseeDTO);
    }

    /**
     * {@code DELETE  /mode-evacuation-eau-usees/:id} : delete the "id" modeEvacuationEauUsee.
     *
     * @param id the id of the modeEvacuationEauUseeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/mode-evacuation-eau-usees/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteModeEvacuationEauUsee(@PathVariable Long id) {
        log.debug("REST request to delete ModeEvacuationEauUsee : {}", id);
        return modeEvacuationEauUseeService
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
     * {@code SEARCH  /_search/mode-evacuation-eau-usees?query=:query} : search for the modeEvacuationEauUsee corresponding
     * to the query.
     *
     * @param query the query of the modeEvacuationEauUsee search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/mode-evacuation-eau-usees")
    public Mono<ResponseEntity<Flux<ModeEvacuationEauUseeDTO>>> searchModeEvacuationEauUsees(
        @RequestParam String query,
        Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of ModeEvacuationEauUsees for query {}", query);
        return modeEvacuationEauUseeService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(modeEvacuationEauUseeService.search(query, pageable)));
    }
}
