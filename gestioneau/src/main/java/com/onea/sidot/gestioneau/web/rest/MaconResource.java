package com.onea.sidot.gestioneau.web.rest;

import com.onea.sidot.gestioneau.repository.MaconRepository;
import com.onea.sidot.gestioneau.service.MaconService;
import com.onea.sidot.gestioneau.service.dto.MaconDTO;
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
 * REST controller for managing {@link com.onea.sidot.gestioneau.domain.Macon}.
 */
@RestController
@RequestMapping("/api")
public class MaconResource {

    private final Logger log = LoggerFactory.getLogger(MaconResource.class);

    private static final String ENTITY_NAME = "gestioneauMacon";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MaconService maconService;

    private final MaconRepository maconRepository;

    public MaconResource(MaconService maconService, MaconRepository maconRepository) {
        this.maconService = maconService;
        this.maconRepository = maconRepository;
    }

    /**
     * {@code POST  /macons} : Create a new macon.
     *
     * @param maconDTO the maconDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new maconDTO, or with status {@code 400 (Bad Request)} if the macon has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/macons")
    public Mono<ResponseEntity<MaconDTO>> createMacon(@Valid @RequestBody MaconDTO maconDTO) throws URISyntaxException {
        log.debug("REST request to save Macon : {}", maconDTO);
        if (maconDTO.getId() != null) {
            throw new BadRequestAlertException("A new macon cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return maconService
            .save(maconDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/macons/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /macons/:id} : Updates an existing macon.
     *
     * @param id the id of the maconDTO to save.
     * @param maconDTO the maconDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated maconDTO,
     * or with status {@code 400 (Bad Request)} if the maconDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the maconDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/macons/{id}")
    public Mono<ResponseEntity<MaconDTO>> updateMacon(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MaconDTO maconDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Macon : {}, {}", id, maconDTO);
        if (maconDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, maconDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return maconRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return maconService
                        .save(maconDTO)
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
     * {@code PATCH  /macons/:id} : Partial updates given fields of an existing macon, field will ignore if it is null
     *
     * @param id the id of the maconDTO to save.
     * @param maconDTO the maconDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated maconDTO,
     * or with status {@code 400 (Bad Request)} if the maconDTO is not valid,
     * or with status {@code 404 (Not Found)} if the maconDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the maconDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/macons/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<MaconDTO>> partialUpdateMacon(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MaconDTO maconDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Macon partially : {}, {}", id, maconDTO);
        if (maconDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, maconDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return maconRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<MaconDTO> result = maconService.partialUpdate(maconDTO);

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
     * {@code GET  /macons} : get all the macons.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of macons in body.
     */
    @GetMapping("/macons")
    public Mono<ResponseEntity<List<MaconDTO>>> getAllMacons(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Macons");
        return maconService
            .countAll()
            .zipWith(maconService.findAll(pageable).collectList())
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
     * {@code GET  /macons/:id} : get the "id" macon.
     *
     * @param id the id of the maconDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the maconDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/macons/{id}")
    public Mono<ResponseEntity<MaconDTO>> getMacon(@PathVariable Long id) {
        log.debug("REST request to get Macon : {}", id);
        Mono<MaconDTO> maconDTO = maconService.findOne(id);
        return ResponseUtil.wrapOrNotFound(maconDTO);
    }

    /**
     * {@code DELETE  /macons/:id} : delete the "id" macon.
     *
     * @param id the id of the maconDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/macons/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteMacon(@PathVariable Long id) {
        log.debug("REST request to delete Macon : {}", id);
        return maconService
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
     * {@code SEARCH  /_search/macons?query=:query} : search for the macon corresponding
     * to the query.
     *
     * @param query the query of the macon search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search/macons")
    public Mono<ResponseEntity<Flux<MaconDTO>>> searchMacons(@RequestParam String query, Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to search for a page of Macons for query {}", query);
        return maconService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page -> PaginationUtil.generatePaginationHttpHeaders(UriComponentsBuilder.fromHttpRequest(request), page))
            .map(headers -> ResponseEntity.ok().headers(headers).body(maconService.search(query, pageable)));
    }
}
