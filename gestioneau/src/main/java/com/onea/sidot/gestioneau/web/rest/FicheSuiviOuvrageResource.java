package com.onea.sidot.gestioneau.web.rest;

import com.onea.sidot.gestioneau.repository.FicheSuiviOuvrageRepository;
import com.onea.sidot.gestioneau.service.FicheSuiviOuvrageService;
import com.onea.sidot.gestioneau.service.dto.FicheSuiviOuvrageDTO;
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
 * REST controller for managing {@link com.onea.sidot.gestioneau.domain.FicheSuiviOuvrage}.
 */
@RestController
@RequestMapping("/api")
public class FicheSuiviOuvrageResource {

    private final Logger log = LoggerFactory.getLogger(FicheSuiviOuvrageResource.class);

    private static final String ENTITY_NAME = "gestioneauFicheSuiviOuvrage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FicheSuiviOuvrageService ficheSuiviOuvrageService;

    private final FicheSuiviOuvrageRepository ficheSuiviOuvrageRepository;

    public FicheSuiviOuvrageResource(
        FicheSuiviOuvrageService ficheSuiviOuvrageService,
        FicheSuiviOuvrageRepository ficheSuiviOuvrageRepository
    ) {
        this.ficheSuiviOuvrageService = ficheSuiviOuvrageService;
        this.ficheSuiviOuvrageRepository = ficheSuiviOuvrageRepository;
    }

    /**
     * {@code POST  /fiche-suivi-ouvrages} : Create a new ficheSuiviOuvrage.
     *
     * @param ficheSuiviOuvrageDTO the ficheSuiviOuvrageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ficheSuiviOuvrageDTO, or with status {@code 400 (Bad Request)} if the ficheSuiviOuvrage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/fiche-suivi-ouvrages")
    public Mono<ResponseEntity<FicheSuiviOuvrageDTO>> createFicheSuiviOuvrage(
        @Valid @RequestBody FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO
    ) throws URISyntaxException {
        log.debug("REST request to save FicheSuiviOuvrage : {}", ficheSuiviOuvrageDTO);
        if (ficheSuiviOuvrageDTO.getId() != null) {
            throw new BadRequestAlertException("A new ficheSuiviOuvrage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return ficheSuiviOuvrageService
            .save(ficheSuiviOuvrageDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/fiche-suivi-ouvrages/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /fiche-suivi-ouvrages/:id} : Updates an existing ficheSuiviOuvrage.
     *
     * @param id the id of the ficheSuiviOuvrageDTO to save.
     * @param ficheSuiviOuvrageDTO the ficheSuiviOuvrageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ficheSuiviOuvrageDTO,
     * or with status {@code 400 (Bad Request)} if the ficheSuiviOuvrageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ficheSuiviOuvrageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/fiche-suivi-ouvrages/{id}")
    public Mono<ResponseEntity<FicheSuiviOuvrageDTO>> updateFicheSuiviOuvrage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO
    ) throws URISyntaxException {
        log.debug("REST request to update FicheSuiviOuvrage : {}, {}", id, ficheSuiviOuvrageDTO);
        if (ficheSuiviOuvrageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ficheSuiviOuvrageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ficheSuiviOuvrageRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return ficheSuiviOuvrageService
                        .save(ficheSuiviOuvrageDTO)
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
     * {@code PATCH  /fiche-suivi-ouvrages/:id} : Partial updates given fields of an existing ficheSuiviOuvrage, field will ignore if it is null
     *
     * @param id the id of the ficheSuiviOuvrageDTO to save.
     * @param ficheSuiviOuvrageDTO the ficheSuiviOuvrageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ficheSuiviOuvrageDTO,
     * or with status {@code 400 (Bad Request)} if the ficheSuiviOuvrageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ficheSuiviOuvrageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ficheSuiviOuvrageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/fiche-suivi-ouvrages/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<FicheSuiviOuvrageDTO>> partialUpdateFicheSuiviOuvrage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update FicheSuiviOuvrage partially : {}, {}", id, ficheSuiviOuvrageDTO);
        if (ficheSuiviOuvrageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ficheSuiviOuvrageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return ficheSuiviOuvrageRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<FicheSuiviOuvrageDTO> result = ficheSuiviOuvrageService.partialUpdate(ficheSuiviOuvrageDTO);

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
     * {@code GET  /fiche-suivi-ouvrages} : get all the ficheSuiviOuvrages.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ficheSuiviOuvrages in body.
     */
    @GetMapping("/fiche-suivi-ouvrages")
    public Mono<ResponseEntity<List<FicheSuiviOuvrageDTO>>> getAllFicheSuiviOuvrages(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of FicheSuiviOuvrages");
        return ficheSuiviOuvrageService
            .countAll()
            .zipWith(ficheSuiviOuvrageService.findAll(pageable).collectList())
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
     * {@code GET  /fiche-suivi-ouvrages/:id} : get the "id" ficheSuiviOuvrage.
     *
     * @param id the id of the ficheSuiviOuvrageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ficheSuiviOuvrageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/fiche-suivi-ouvrages/{id}")
    public Mono<ResponseEntity<FicheSuiviOuvrageDTO>> getFicheSuiviOuvrage(@PathVariable Long id) {
        log.debug("REST request to get FicheSuiviOuvrage : {}", id);
        Mono<FicheSuiviOuvrageDTO> ficheSuiviOuvrageDTO = ficheSuiviOuvrageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ficheSuiviOuvrageDTO);
    }

    /**
     * {@code DELETE  /fiche-suivi-ouvrages/:id} : delete the "id" ficheSuiviOuvrage.
     *
     * @param id the id of the ficheSuiviOuvrageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/fiche-suivi-ouvrages/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteFicheSuiviOuvrage(@PathVariable Long id) {
        log.debug("REST request to delete FicheSuiviOuvrage : {}", id);
        return ficheSuiviOuvrageService
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
     * {@code SEARCH  /_search/fiche-suivi-ouvrages?query=:query} : search for the ficheSuiviOuvrage corresponding
     * to the query.
     *
     * @param query the query of the ficheSuiviOuvrage search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/fiche-suivi-ouvrages")
    public Mono<ResponseEntity<Flux<FicheSuiviOuvrageDTO>>> searchFicheSuiviOuvrages(
        @RequestParam String query,
        Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of FicheSuiviOuvrages for query {}", query);
        return ficheSuiviOuvrageService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(ficheSuiviOuvrageService.search(query, pageable)));
    }
}
