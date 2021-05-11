package com.onea.sidot.gestioneau.web.rest;

import com.onea.sidot.gestioneau.repository.AnneeRepository;
import com.onea.sidot.gestioneau.service.AnneeService;
import com.onea.sidot.gestioneau.service.dto.AnneeDTO;
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
 * REST controller for managing {@link com.onea.sidot.gestioneau.domain.Annee}.
 */
@RestController
@RequestMapping("/api")
public class AnneeResource {

    private final Logger log = LoggerFactory.getLogger(AnneeResource.class);

    private static final String ENTITY_NAME = "gestioneauAnnee";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AnneeService anneeService;

    private final AnneeRepository anneeRepository;

    public AnneeResource(AnneeService anneeService, AnneeRepository anneeRepository) {
        this.anneeService = anneeService;
        this.anneeRepository = anneeRepository;
    }

    /**
     * {@code POST  /annees} : Create a new annee.
     *
     * @param anneeDTO the anneeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new anneeDTO, or with status {@code 400 (Bad Request)} if the annee has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/annees")
    public Mono<ResponseEntity<AnneeDTO>> createAnnee(@Valid @RequestBody AnneeDTO anneeDTO) throws URISyntaxException {
        log.debug("REST request to save Annee : {}", anneeDTO);
        if (anneeDTO.getId() != null) {
            throw new BadRequestAlertException("A new annee cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return anneeService
            .save(anneeDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/annees/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /annees/:id} : Updates an existing annee.
     *
     * @param id the id of the anneeDTO to save.
     * @param anneeDTO the anneeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated anneeDTO,
     * or with status {@code 400 (Bad Request)} if the anneeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the anneeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/annees/{id}")
    public Mono<ResponseEntity<AnneeDTO>> updateAnnee(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AnneeDTO anneeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Annee : {}, {}", id, anneeDTO);
        if (anneeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, anneeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return anneeRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return anneeService
                        .save(anneeDTO)
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
     * {@code PATCH  /annees/:id} : Partial updates given fields of an existing annee, field will ignore if it is null
     *
     * @param id the id of the anneeDTO to save.
     * @param anneeDTO the anneeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated anneeDTO,
     * or with status {@code 400 (Bad Request)} if the anneeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the anneeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the anneeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/annees/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<AnneeDTO>> partialUpdateAnnee(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AnneeDTO anneeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Annee partially : {}, {}", id, anneeDTO);
        if (anneeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, anneeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return anneeRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<AnneeDTO> result = anneeService.partialUpdate(anneeDTO);

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
     * {@code GET  /annees} : get all the annees.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of annees in body.
     */
    @GetMapping("/annees")
    public Mono<ResponseEntity<List<AnneeDTO>>> getAllAnnees(
        Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false) String filter
    ) {
        if ("prevision-is-null".equals(filter)) {
            log.debug("REST request to get all Annees where prevision is null");
            return anneeService.findAllWherePrevisionIsNull().collectList().map(ResponseEntity::ok);
        }
        log.debug("REST request to get a page of Annees");
        return anneeService
            .countAll()
            .zipWith(anneeService.findAll(pageable).collectList())
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
     * {@code GET  /annees/:id} : get the "id" annee.
     *
     * @param id the id of the anneeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the anneeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/annees/{id}")
    public Mono<ResponseEntity<AnneeDTO>> getAnnee(@PathVariable Long id) {
        log.debug("REST request to get Annee : {}", id);
        Mono<AnneeDTO> anneeDTO = anneeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(anneeDTO);
    }

    /**
     * {@code DELETE  /annees/:id} : delete the "id" annee.
     *
     * @param id the id of the anneeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/annees/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteAnnee(@PathVariable Long id) {
        log.debug("REST request to delete Annee : {}", id);
        return anneeService
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
     * {@code SEARCH  /_search/annees?query=:query} : search for the annee corresponding
     * to the query.
     *
     * @param query the query of the annee search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/annees")
    public Mono<ResponseEntity<Flux<AnneeDTO>>> searchAnnees(@RequestParam String query, Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to search for a page of Annees for query {}", query);
        return anneeService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(anneeService.search(query, pageable)));
    }
}
