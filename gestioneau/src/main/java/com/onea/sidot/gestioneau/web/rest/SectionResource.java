package com.onea.sidot.gestioneau.web.rest;

import com.onea.sidot.gestioneau.repository.SectionRepository;
import com.onea.sidot.gestioneau.service.SectionService;
import com.onea.sidot.gestioneau.service.dto.SectionDTO;
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
 * REST controller for managing {@link com.onea.sidot.gestioneau.domain.Section}.
 */
@RestController
@RequestMapping("/api")
public class SectionResource {

    private final Logger log = LoggerFactory.getLogger(SectionResource.class);

    private static final String ENTITY_NAME = "gestioneauSection";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SectionService sectionService;

    private final SectionRepository sectionRepository;

    public SectionResource(SectionService sectionService, SectionRepository sectionRepository) {
        this.sectionService = sectionService;
        this.sectionRepository = sectionRepository;
    }

    /**
     * {@code POST  /sections} : Create a new section.
     *
     * @param sectionDTO the sectionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sectionDTO, or with status {@code 400 (Bad Request)} if the section has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sections")
    public Mono<ResponseEntity<SectionDTO>> createSection(@Valid @RequestBody SectionDTO sectionDTO) throws URISyntaxException {
        log.debug("REST request to save Section : {}", sectionDTO);
        if (sectionDTO.getId() != null) {
            throw new BadRequestAlertException("A new section cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return sectionService
            .save(sectionDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/sections/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /sections/:id} : Updates an existing section.
     *
     * @param id the id of the sectionDTO to save.
     * @param sectionDTO the sectionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sectionDTO,
     * or with status {@code 400 (Bad Request)} if the sectionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sectionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sections/{id}")
    public Mono<ResponseEntity<SectionDTO>> updateSection(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SectionDTO sectionDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Section : {}, {}", id, sectionDTO);
        if (sectionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sectionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return sectionRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return sectionService
                        .save(sectionDTO)
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
     * {@code PATCH  /sections/:id} : Partial updates given fields of an existing section, field will ignore if it is null
     *
     * @param id the id of the sectionDTO to save.
     * @param sectionDTO the sectionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sectionDTO,
     * or with status {@code 400 (Bad Request)} if the sectionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the sectionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the sectionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/sections/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<SectionDTO>> partialUpdateSection(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SectionDTO sectionDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Section partially : {}, {}", id, sectionDTO);
        if (sectionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sectionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return sectionRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<SectionDTO> result = sectionService.partialUpdate(sectionDTO);

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
     * {@code GET  /sections} : get all the sections.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sections in body.
     */
    @GetMapping("/sections")
    public Mono<ResponseEntity<List<SectionDTO>>> getAllSections(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Sections");
        return sectionService
            .countAll()
            .zipWith(sectionService.findAll(pageable).collectList())
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
     * {@code GET  /sections/:id} : get the "id" section.
     *
     * @param id the id of the sectionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sectionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sections/{id}")
    public Mono<ResponseEntity<SectionDTO>> getSection(@PathVariable Long id) {
        log.debug("REST request to get Section : {}", id);
        Mono<SectionDTO> sectionDTO = sectionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sectionDTO);
    }

    /**
     * {@code DELETE  /sections/:id} : delete the "id" section.
     *
     * @param id the id of the sectionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sections/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteSection(@PathVariable Long id) {
        log.debug("REST request to delete Section : {}", id);
        return sectionService
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
     * {@code SEARCH  /_search/sections?query=:query} : search for the section corresponding
     * to the query.
     *
     * @param query the query of the section search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/sections")
    public Mono<ResponseEntity<Flux<SectionDTO>>> searchSections(@RequestParam String query, Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to search for a page of Sections for query {}", query);
        return sectionService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(sectionService.search(query, pageable)));
    }
}
