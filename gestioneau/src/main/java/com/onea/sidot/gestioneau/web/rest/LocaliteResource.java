package com.onea.sidot.gestioneau.web.rest;

import com.onea.sidot.gestioneau.repository.LocaliteRepository;
import com.onea.sidot.gestioneau.service.LocaliteService;
import com.onea.sidot.gestioneau.service.dto.LocaliteDTO;
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
 * REST controller for managing {@link com.onea.sidot.gestioneau.domain.Localite}.
 */
@RestController
@RequestMapping("/api")
public class LocaliteResource {

    private final Logger log = LoggerFactory.getLogger(LocaliteResource.class);

    private static final String ENTITY_NAME = "gestioneauLocalite";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LocaliteService localiteService;

    private final LocaliteRepository localiteRepository;

    public LocaliteResource(LocaliteService localiteService, LocaliteRepository localiteRepository) {
        this.localiteService = localiteService;
        this.localiteRepository = localiteRepository;
    }

    /**
     * {@code POST  /localites} : Create a new localite.
     *
     * @param localiteDTO the localiteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new localiteDTO, or with status {@code 400 (Bad Request)} if the localite has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/localites")
    public Mono<ResponseEntity<LocaliteDTO>> createLocalite(@Valid @RequestBody LocaliteDTO localiteDTO) throws URISyntaxException {
        log.debug("REST request to save Localite : {}", localiteDTO);
        if (localiteDTO.getId() != null) {
            throw new BadRequestAlertException("A new localite cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return localiteService
            .save(localiteDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/localites/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /localites/:id} : Updates an existing localite.
     *
     * @param id the id of the localiteDTO to save.
     * @param localiteDTO the localiteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated localiteDTO,
     * or with status {@code 400 (Bad Request)} if the localiteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the localiteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/localites/{id}")
    public Mono<ResponseEntity<LocaliteDTO>> updateLocalite(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LocaliteDTO localiteDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Localite : {}, {}", id, localiteDTO);
        if (localiteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, localiteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return localiteRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return localiteService
                        .save(localiteDTO)
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
     * {@code PATCH  /localites/:id} : Partial updates given fields of an existing localite, field will ignore if it is null
     *
     * @param id the id of the localiteDTO to save.
     * @param localiteDTO the localiteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated localiteDTO,
     * or with status {@code 400 (Bad Request)} if the localiteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the localiteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the localiteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/localites/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<LocaliteDTO>> partialUpdateLocalite(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LocaliteDTO localiteDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Localite partially : {}, {}", id, localiteDTO);
        if (localiteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, localiteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return localiteRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<LocaliteDTO> result = localiteService.partialUpdate(localiteDTO);

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
     * {@code GET  /localites} : get all the localites.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of localites in body.
     */
    @GetMapping("/localites")
    public Mono<ResponseEntity<List<LocaliteDTO>>> getAllLocalites(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Localites");
        return localiteService
            .countAll()
            .zipWith(localiteService.findAll(pageable).collectList())
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
     * {@code GET  /localites/:id} : get the "id" localite.
     *
     * @param id the id of the localiteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the localiteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/localites/{id}")
    public Mono<ResponseEntity<LocaliteDTO>> getLocalite(@PathVariable Long id) {
        log.debug("REST request to get Localite : {}", id);
        Mono<LocaliteDTO> localiteDTO = localiteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(localiteDTO);
    }

    /**
     * {@code DELETE  /localites/:id} : delete the "id" localite.
     *
     * @param id the id of the localiteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/localites/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteLocalite(@PathVariable Long id) {
        log.debug("REST request to delete Localite : {}", id);
        return localiteService
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
     * {@code SEARCH  /_search/localites?query=:query} : search for the localite corresponding
     * to the query.
     *
     * @param query the query of the localite search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/localites")
    public Mono<ResponseEntity<Flux<LocaliteDTO>>> searchLocalites(
        @RequestParam String query,
        Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to search for a page of Localites for query {}", query);
        return localiteService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(localiteService.search(query, pageable)));
    }
}
