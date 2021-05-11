package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.NatureOuvrage;
import com.onea.sidot.gestioneau.repository.NatureOuvrageRepository;
import com.onea.sidot.gestioneau.repository.search.NatureOuvrageSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.NatureOuvrageDTO;
import com.onea.sidot.gestioneau.service.mapper.NatureOuvrageMapper;
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
 * Integration tests for the {@link NatureOuvrageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class NatureOuvrageResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/nature-ouvrages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/nature-ouvrages";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NatureOuvrageRepository natureOuvrageRepository;

    @Autowired
    private NatureOuvrageMapper natureOuvrageMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.NatureOuvrageSearchRepositoryMockConfiguration
     */
    @Autowired
    private NatureOuvrageSearchRepository mockNatureOuvrageSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private NatureOuvrage natureOuvrage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NatureOuvrage createEntity(EntityManager em) {
        NatureOuvrage natureOuvrage = new NatureOuvrage().libelle(DEFAULT_LIBELLE);
        return natureOuvrage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NatureOuvrage createUpdatedEntity(EntityManager em) {
        NatureOuvrage natureOuvrage = new NatureOuvrage().libelle(UPDATED_LIBELLE);
        return natureOuvrage;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(NatureOuvrage.class).block();
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
        natureOuvrage = createEntity(em);
    }

    @Test
    void createNatureOuvrage() throws Exception {
        int databaseSizeBeforeCreate = natureOuvrageRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockNatureOuvrageSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the NatureOuvrage
        NatureOuvrageDTO natureOuvrageDTO = natureOuvrageMapper.toDto(natureOuvrage);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(natureOuvrageDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the NatureOuvrage in the database
        List<NatureOuvrage> natureOuvrageList = natureOuvrageRepository.findAll().collectList().block();
        assertThat(natureOuvrageList).hasSize(databaseSizeBeforeCreate + 1);
        NatureOuvrage testNatureOuvrage = natureOuvrageList.get(natureOuvrageList.size() - 1);
        assertThat(testNatureOuvrage.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the NatureOuvrage in Elasticsearch
        verify(mockNatureOuvrageSearchRepository, times(1)).save(testNatureOuvrage);
    }

    @Test
    void createNatureOuvrageWithExistingId() throws Exception {
        // Create the NatureOuvrage with an existing ID
        natureOuvrage.setId(1L);
        NatureOuvrageDTO natureOuvrageDTO = natureOuvrageMapper.toDto(natureOuvrage);

        int databaseSizeBeforeCreate = natureOuvrageRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(natureOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the NatureOuvrage in the database
        List<NatureOuvrage> natureOuvrageList = natureOuvrageRepository.findAll().collectList().block();
        assertThat(natureOuvrageList).hasSize(databaseSizeBeforeCreate);

        // Validate the NatureOuvrage in Elasticsearch
        verify(mockNatureOuvrageSearchRepository, times(0)).save(natureOuvrage);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = natureOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        natureOuvrage.setLibelle(null);

        // Create the NatureOuvrage, which fails.
        NatureOuvrageDTO natureOuvrageDTO = natureOuvrageMapper.toDto(natureOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(natureOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<NatureOuvrage> natureOuvrageList = natureOuvrageRepository.findAll().collectList().block();
        assertThat(natureOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllNatureOuvrages() {
        // Initialize the database
        natureOuvrageRepository.save(natureOuvrage).block();

        // Get all the natureOuvrageList
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
            .value(hasItem(natureOuvrage.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }

    @Test
    void getNatureOuvrage() {
        // Initialize the database
        natureOuvrageRepository.save(natureOuvrage).block();

        // Get the natureOuvrage
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, natureOuvrage.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(natureOuvrage.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE));
    }

    @Test
    void getNonExistingNatureOuvrage() {
        // Get the natureOuvrage
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewNatureOuvrage() throws Exception {
        // Configure the mock search repository
        when(mockNatureOuvrageSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        natureOuvrageRepository.save(natureOuvrage).block();

        int databaseSizeBeforeUpdate = natureOuvrageRepository.findAll().collectList().block().size();

        // Update the natureOuvrage
        NatureOuvrage updatedNatureOuvrage = natureOuvrageRepository.findById(natureOuvrage.getId()).block();
        updatedNatureOuvrage.libelle(UPDATED_LIBELLE);
        NatureOuvrageDTO natureOuvrageDTO = natureOuvrageMapper.toDto(updatedNatureOuvrage);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, natureOuvrageDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(natureOuvrageDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the NatureOuvrage in the database
        List<NatureOuvrage> natureOuvrageList = natureOuvrageRepository.findAll().collectList().block();
        assertThat(natureOuvrageList).hasSize(databaseSizeBeforeUpdate);
        NatureOuvrage testNatureOuvrage = natureOuvrageList.get(natureOuvrageList.size() - 1);
        assertThat(testNatureOuvrage.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the NatureOuvrage in Elasticsearch
        verify(mockNatureOuvrageSearchRepository).save(testNatureOuvrage);
    }

    @Test
    void putNonExistingNatureOuvrage() throws Exception {
        int databaseSizeBeforeUpdate = natureOuvrageRepository.findAll().collectList().block().size();
        natureOuvrage.setId(count.incrementAndGet());

        // Create the NatureOuvrage
        NatureOuvrageDTO natureOuvrageDTO = natureOuvrageMapper.toDto(natureOuvrage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, natureOuvrageDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(natureOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the NatureOuvrage in the database
        List<NatureOuvrage> natureOuvrageList = natureOuvrageRepository.findAll().collectList().block();
        assertThat(natureOuvrageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the NatureOuvrage in Elasticsearch
        verify(mockNatureOuvrageSearchRepository, times(0)).save(natureOuvrage);
    }

    @Test
    void putWithIdMismatchNatureOuvrage() throws Exception {
        int databaseSizeBeforeUpdate = natureOuvrageRepository.findAll().collectList().block().size();
        natureOuvrage.setId(count.incrementAndGet());

        // Create the NatureOuvrage
        NatureOuvrageDTO natureOuvrageDTO = natureOuvrageMapper.toDto(natureOuvrage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(natureOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the NatureOuvrage in the database
        List<NatureOuvrage> natureOuvrageList = natureOuvrageRepository.findAll().collectList().block();
        assertThat(natureOuvrageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the NatureOuvrage in Elasticsearch
        verify(mockNatureOuvrageSearchRepository, times(0)).save(natureOuvrage);
    }

    @Test
    void putWithMissingIdPathParamNatureOuvrage() throws Exception {
        int databaseSizeBeforeUpdate = natureOuvrageRepository.findAll().collectList().block().size();
        natureOuvrage.setId(count.incrementAndGet());

        // Create the NatureOuvrage
        NatureOuvrageDTO natureOuvrageDTO = natureOuvrageMapper.toDto(natureOuvrage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(natureOuvrageDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the NatureOuvrage in the database
        List<NatureOuvrage> natureOuvrageList = natureOuvrageRepository.findAll().collectList().block();
        assertThat(natureOuvrageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the NatureOuvrage in Elasticsearch
        verify(mockNatureOuvrageSearchRepository, times(0)).save(natureOuvrage);
    }

    @Test
    void partialUpdateNatureOuvrageWithPatch() throws Exception {
        // Initialize the database
        natureOuvrageRepository.save(natureOuvrage).block();

        int databaseSizeBeforeUpdate = natureOuvrageRepository.findAll().collectList().block().size();

        // Update the natureOuvrage using partial update
        NatureOuvrage partialUpdatedNatureOuvrage = new NatureOuvrage();
        partialUpdatedNatureOuvrage.setId(natureOuvrage.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNatureOuvrage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedNatureOuvrage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the NatureOuvrage in the database
        List<NatureOuvrage> natureOuvrageList = natureOuvrageRepository.findAll().collectList().block();
        assertThat(natureOuvrageList).hasSize(databaseSizeBeforeUpdate);
        NatureOuvrage testNatureOuvrage = natureOuvrageList.get(natureOuvrageList.size() - 1);
        assertThat(testNatureOuvrage.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
    }

    @Test
    void fullUpdateNatureOuvrageWithPatch() throws Exception {
        // Initialize the database
        natureOuvrageRepository.save(natureOuvrage).block();

        int databaseSizeBeforeUpdate = natureOuvrageRepository.findAll().collectList().block().size();

        // Update the natureOuvrage using partial update
        NatureOuvrage partialUpdatedNatureOuvrage = new NatureOuvrage();
        partialUpdatedNatureOuvrage.setId(natureOuvrage.getId());

        partialUpdatedNatureOuvrage.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNatureOuvrage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedNatureOuvrage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the NatureOuvrage in the database
        List<NatureOuvrage> natureOuvrageList = natureOuvrageRepository.findAll().collectList().block();
        assertThat(natureOuvrageList).hasSize(databaseSizeBeforeUpdate);
        NatureOuvrage testNatureOuvrage = natureOuvrageList.get(natureOuvrageList.size() - 1);
        assertThat(testNatureOuvrage.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void patchNonExistingNatureOuvrage() throws Exception {
        int databaseSizeBeforeUpdate = natureOuvrageRepository.findAll().collectList().block().size();
        natureOuvrage.setId(count.incrementAndGet());

        // Create the NatureOuvrage
        NatureOuvrageDTO natureOuvrageDTO = natureOuvrageMapper.toDto(natureOuvrage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, natureOuvrageDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(natureOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the NatureOuvrage in the database
        List<NatureOuvrage> natureOuvrageList = natureOuvrageRepository.findAll().collectList().block();
        assertThat(natureOuvrageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the NatureOuvrage in Elasticsearch
        verify(mockNatureOuvrageSearchRepository, times(0)).save(natureOuvrage);
    }

    @Test
    void patchWithIdMismatchNatureOuvrage() throws Exception {
        int databaseSizeBeforeUpdate = natureOuvrageRepository.findAll().collectList().block().size();
        natureOuvrage.setId(count.incrementAndGet());

        // Create the NatureOuvrage
        NatureOuvrageDTO natureOuvrageDTO = natureOuvrageMapper.toDto(natureOuvrage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(natureOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the NatureOuvrage in the database
        List<NatureOuvrage> natureOuvrageList = natureOuvrageRepository.findAll().collectList().block();
        assertThat(natureOuvrageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the NatureOuvrage in Elasticsearch
        verify(mockNatureOuvrageSearchRepository, times(0)).save(natureOuvrage);
    }

    @Test
    void patchWithMissingIdPathParamNatureOuvrage() throws Exception {
        int databaseSizeBeforeUpdate = natureOuvrageRepository.findAll().collectList().block().size();
        natureOuvrage.setId(count.incrementAndGet());

        // Create the NatureOuvrage
        NatureOuvrageDTO natureOuvrageDTO = natureOuvrageMapper.toDto(natureOuvrage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(natureOuvrageDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the NatureOuvrage in the database
        List<NatureOuvrage> natureOuvrageList = natureOuvrageRepository.findAll().collectList().block();
        assertThat(natureOuvrageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the NatureOuvrage in Elasticsearch
        verify(mockNatureOuvrageSearchRepository, times(0)).save(natureOuvrage);
    }

    @Test
    void deleteNatureOuvrage() {
        // Configure the mock search repository
        when(mockNatureOuvrageSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockNatureOuvrageSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        natureOuvrageRepository.save(natureOuvrage).block();

        int databaseSizeBeforeDelete = natureOuvrageRepository.findAll().collectList().block().size();

        // Delete the natureOuvrage
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, natureOuvrage.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<NatureOuvrage> natureOuvrageList = natureOuvrageRepository.findAll().collectList().block();
        assertThat(natureOuvrageList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the NatureOuvrage in Elasticsearch
        verify(mockNatureOuvrageSearchRepository, times(1)).deleteById(natureOuvrage.getId());
    }

    @Test
    void searchNatureOuvrage() {
        // Configure the mock search repository
        when(mockNatureOuvrageSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockNatureOuvrageSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        natureOuvrageRepository.save(natureOuvrage).block();
        when(mockNatureOuvrageSearchRepository.search("id:" + natureOuvrage.getId(), PageRequest.of(0, 20)))
            .thenReturn(Flux.just(natureOuvrage));

        // Search the natureOuvrage
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + natureOuvrage.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(natureOuvrage.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }
}
