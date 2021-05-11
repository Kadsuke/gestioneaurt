package com.onea.sidot.gestioneau.web.rest;

import com.onea.sidot.gestioneau.repository.SecteurRepository;
import com.onea.sidot.gestioneau.service.SecteurService;
import com.onea.sidot.gestioneau.service.dto.SecteurDTO;
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
 * REST controller for managing {@link com.onea.sidot.gestioneau.domain.Secteur}.
 */
@RestController
@RequestMapping("/api")
public class SecteurResource {

    private final Logger log = LoggerFactory.getLogger(SecteurResource.class);

    private static final String ENTITY_NAME = "gestioneauSecteur";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SecteurService secteurService;

    private final SecteurRepository secteurRepository;

    public SecteurResource(SecteurService secteurService, SecteurRepository secteurRepository) {
        this.secteurService = secteurService;
        this.secteurRepository = secteurRepository;
    }

    /**
     * {@code POST  /secteurs} : Create a new secteur.
     *
     * @param secteurDTO the secteurDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new secteurDTO, or with status {@code 400 (Bad Request)} if the secteur has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/secteurs")
    public Mono<ResponseEntity<SecteurDTO>> createSecteur(@Valid @RequestBody SecteurDTO secteurDTO) throws URISyntaxException {
        log.debug("REST request to save Secteur : {}", secteurDTO);
        if (secteurDTO.getId() != null) {
            throw new BadRequestAlertException("A new secteur cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return secteurService
            .save(secteurDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/secteurs/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /secteurs/:id} : Updates an existing secteur.
     *
     * @param id the id of the secteurDTO to save.
     * @param secteurDTO the secteurDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated secteurDTO,
     * or with status {@code 400 (Bad Request)} if the secteurDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the secteurDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/secteurs/{id}")
    public Mono<ResponseEntity<SecteurDTO>> updateSecteur(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SecteurDTO secteurDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Secteur : {}, {}", id, secteurDTO);
        if (secteurDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, secteurDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return secteurRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return secteurService
                        .save(secteurDTO)
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
     * {@code PATCH  /secteurs/:id} : Partial updates given fields of an existing secteur, field will ignore if it is null
     *
     * @param id the id of the secteurDTO to save.
     * @param secteurDTO the secteurDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated secteurDTO,
     * or with status {@code 400 (Bad Request)} if the secteurDTO is not valid,
     * or with status {@code 404 (Not Found)} if the secteurDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the secteurDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/secteurs/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<SecteurDTO>> partialUpdateSecteur(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SecteurDTO secteurDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Secteur partially : {}, {}", id, secteurDTO);
        if (secteurDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, secteurDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return secteurRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<SecteurDTO> result = secteurService.partialUpdate(secteurDTO);

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
     * {@code GET  /secteurs} : get all the secteurs.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of secteurs in body.
     */
    @GetMapping("/secteurs")
    public Mono<ResponseEntity<List<SecteurDTO>>> getAllSecteurs(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Secteurs");
        return secteurService
            .countAll()
            .zipWith(secteurService.findAll(pageable).collectList())
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
     * {@code GET  /secteurs/:id} : get the "id" secteur.
     *
     * @param id the id of the secteurDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the secteurDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/secteurs/{id}")
    public Mono<ResponseEntity<SecteurDTO>> getSecteur(@PathVariable Long id) {
        log.debug("REST request to get Secteur : {}", id);
        Mono<SecteurDTO> secteurDTO = secteurService.findOne(id);
        return ResponseUtil.wrapOrNotFound(secteurDTO);
    }

    /**
     * {@code DELETE  /secteurs/:id} : delete the "id" secteur.
     *
     * @param id the id of the secteurDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/secteurs/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteSecteur(@PathVariable Long id) {
        log.debug("REST request to delete Secteur : {}", id);
        return secteurService
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
     * {@code SEARCH  /_search/secteurs?query=:query} : search for the secteur corresponding
     * to the query.
     *
     * @param query the query of the secteur search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/secteurs")
    public Mono<ResponseEntity<Flux<SecteurDTO>>> searchSecteurs(@RequestParam String query, Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to search for a page of Secteurs for query {}", query);
        return secteurService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(secteurService.search(query, pageable)));
    }
}
