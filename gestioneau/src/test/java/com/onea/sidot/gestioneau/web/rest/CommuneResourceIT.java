package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.Commune;
import com.onea.sidot.gestioneau.repository.CommuneRepository;
import com.onea.sidot.gestioneau.repository.search.CommuneSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.CommuneDTO;
import com.onea.sidot.gestioneau.service.mapper.CommuneMapper;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link CommuneResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class CommuneResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/communes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/communes";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CommuneRepository communeRepository;

    @Autowired
    private CommuneMapper communeMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.CommuneSearchRepositoryMockConfiguration
     */
    @Autowired
    private CommuneSearchRepository mockCommuneSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Commune commune;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Commune createEntity(EntityManager em) {
        Commune commune = new Commune().libelle(DEFAULT_LIBELLE);
        return commune;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Commune createUpdatedEntity(EntityManager em) {
        Commune commune = new Commune().libelle(UPDATED_LIBELLE);
        return commune;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Commune.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        commune = createEntity(em);
    }

    @Test
    void createCommune() throws Exception {
        int databaseSizeBeforeCreate = communeRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockCommuneSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Commune
        CommuneDTO communeDTO = communeMapper.toDto(commune);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(communeDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Commune in the database
        List<Commune> communeList = communeRepository.findAll().collectList().block();
        assertThat(communeList).hasSize(databaseSizeBeforeCreate + 1);
        Commune testCommune = communeList.get(communeList.size() - 1);
        assertThat(testCommune.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the Commune in Elasticsearch
        verify(mockCommuneSearchRepository, times(1)).save(testCommune);
    }

    @Test
    void createCommuneWithExistingId() throws Exception {
        // Create the Commune with an existing ID
        commune.setId(1L);
        CommuneDTO communeDTO = communeMapper.toDto(commune);

        int databaseSizeBeforeCreate = communeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(communeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commune in the database
        List<Commune> communeList = communeRepository.findAll().collectList().block();
        assertThat(communeList).hasSize(databaseSizeBeforeCreate);

        // Validate the Commune in Elasticsearch
        verify(mockCommuneSearchRepository, times(0)).save(commune);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = communeRepository.findAll().collectList().block().size();
        // set the field null
        commune.setLibelle(null);

        // Create the Commune, which fails.
        CommuneDTO communeDTO = communeMapper.toDto(commune);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(communeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Commune> communeList = communeRepository.findAll().collectList().block();
        assertThat(communeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCommunes() {
        // Initialize the database
        communeRepository.save(commune).block();

        // Get all the communeList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(commune.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }

    @Test
    void getCommune() {
        // Initialize the database
        communeRepository.save(commune).block();

        // Get the commune
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, commune.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(commune.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE));
    }

    @Test
    void getNonExistingCommune() {
        // Get the commune
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCommune() throws Exception {
        // Configure the mock search repository
        when(mockCommuneSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        communeRepository.save(commune).block();

        int databaseSizeBeforeUpdate = communeRepository.findAll().collectList().block().size();

        // Update the commune
        Commune updatedCommune = communeRepository.findById(commune.getId()).block();
        updatedCommune.libelle(UPDATED_LIBELLE);
        CommuneDTO communeDTO = communeMapper.toDto(updatedCommune);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, communeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(communeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Commune in the database
        List<Commune> communeList = communeRepository.findAll().collectList().block();
        assertThat(communeList).hasSize(databaseSizeBeforeUpdate);
        Commune testCommune = communeList.get(communeList.size() - 1);
        assertThat(testCommune.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the Commune in Elasticsearch
        verify(mockCommuneSearchRepository).save(testCommune);
    }

    @Test
    void putNonExistingCommune() throws Exception {
        int databaseSizeBeforeUpdate = communeRepository.findAll().collectList().block().size();
        commune.setId(count.incrementAndGet());

        // Create the Commune
        CommuneDTO communeDTO = communeMapper.toDto(commune);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, communeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(communeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commune in the database
        List<Commune> communeList = communeRepository.findAll().collectList().block();
        assertThat(communeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Commune in Elasticsearch
        verify(mockCommuneSearchRepository, times(0)).save(commune);
    }

    @Test
    void putWithIdMismatchCommune() throws Exception {
        int databaseSizeBeforeUpdate = communeRepository.findAll().collectList().block().size();
        commune.setId(count.incrementAndGet());

        // Create the Commune
        CommuneDTO communeDTO = communeMapper.toDto(commune);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(communeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commune in the database
        List<Commune> communeList = communeRepository.findAll().collectList().block();
        assertThat(communeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Commune in Elasticsearch
        verify(mockCommuneSearchRepository, times(0)).save(commune);
    }

    @Test
    void putWithMissingIdPathParamCommune() throws Exception {
        int databaseSizeBeforeUpdate = communeRepository.findAll().collectList().block().size();
        commune.setId(count.incrementAndGet());

        // Create the Commune
        CommuneDTO communeDTO = communeMapper.toDto(commune);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(communeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Commune in the database
        List<Commune> communeList = communeRepository.findAll().collectList().block();
        assertThat(communeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Commune in Elasticsearch
        verify(mockCommuneSearchRepository, times(0)).save(commune);
    }

    @Test
    void partialUpdateCommuneWithPatch() throws Exception {
        // Initialize the database
        communeRepository.save(commune).block();

        int databaseSizeBeforeUpdate = communeRepository.findAll().collectList().block().size();

        // Update the commune using partial update
        Commune partialUpdatedCommune = new Commune();
        partialUpdatedCommune.setId(commune.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCommune.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCommune))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Commune in the database
        List<Commune> communeList = communeRepository.findAll().collectList().block();
        assertThat(communeList).hasSize(databaseSizeBeforeUpdate);
        Commune testCommune = communeList.get(communeList.size() - 1);
        assertThat(testCommune.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
    }

    @Test
    void fullUpdateCommuneWithPatch() throws Exception {
        // Initialize the database
        communeRepository.save(commune).block();

        int databaseSizeBeforeUpdate = communeRepository.findAll().collectList().block().size();

        // Update the commune using partial update
        Commune partialUpdatedCommune = new Commune();
        partialUpdatedCommune.setId(commune.getId());

        partialUpdatedCommune.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCommune.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCommune))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Commune in the database
        List<Commune> communeList = communeRepository.findAll().collectList().block();
        assertThat(communeList).hasSize(databaseSizeBeforeUpdate);
        Commune testCommune = communeList.get(communeList.size() - 1);
        assertThat(testCommune.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void patchNonExistingCommune() throws Exception {
        int databaseSizeBeforeUpdate = communeRepository.findAll().collectList().block().size();
        commune.setId(count.incrementAndGet());

        // Create the Commune
        CommuneDTO communeDTO = communeMapper.toDto(commune);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, communeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(communeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commune in the database
        List<Commune> communeList = communeRepository.findAll().collectList().block();
        assertThat(communeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Commune in Elasticsearch
        verify(mockCommuneSearchRepository, times(0)).save(commune);
    }

    @Test
    void patchWithIdMismatchCommune() throws Exception {
        int databaseSizeBeforeUpdate = communeRepository.findAll().collectList().block().size();
        commune.setId(count.incrementAndGet());

        // Create the Commune
        CommuneDTO communeDTO = communeMapper.toDto(commune);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(communeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commune in the database
        List<Commune> communeList = communeRepository.findAll().collectList().block();
        assertThat(communeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Commune in Elasticsearch
        verify(mockCommuneSearchRepository, times(0)).save(commune);
    }

    @Test
    void patchWithMissingIdPathParamCommune() throws Exception {
        int databaseSizeBeforeUpdate = communeRepository.findAll().collectList().block().size();
        commune.setId(count.incrementAndGet());

        // Create the Commune
        CommuneDTO communeDTO = communeMapper.toDto(commune);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(communeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Commune in the database
        List<Commune> communeList = communeRepository.findAll().collectList().block();
        assertThat(communeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Commune in Elasticsearch
        verify(mockCommuneSearchRepository, times(0)).save(commune);
    }

    @Test
    void deleteCommune() {
        // Configure the mock search repository
        when(mockCommuneSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockCommuneSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        communeRepository.save(commune).block();

        int databaseSizeBeforeDelete = communeRepository.findAll().collectList().block().size();

        // Delete the commune
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, commune.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Commune> communeList = communeRepository.findAll().collectList().block();
        assertThat(communeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Commune in Elasticsearch
        verify(mockCommuneSearchRepository, times(1)).deleteById(commune.getId());
    }

    @Test
    void searchCommune() {
        // Configure the mock search repository
        when(mockCommuneSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockCommuneSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        communeRepository.save(commune).block();
        when(mockCommuneSearchRepository.search("id:" + commune.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(commune));

        // Search the commune
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + commune.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(commune.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }
}
