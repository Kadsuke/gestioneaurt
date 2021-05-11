package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.Prefabricant;
import com.onea.sidot.gestioneau.repository.PrefabricantRepository;
import com.onea.sidot.gestioneau.repository.search.PrefabricantSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.PrefabricantDTO;
import com.onea.sidot.gestioneau.service.mapper.PrefabricantMapper;
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
 * Integration tests for the {@link PrefabricantResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class PrefabricantResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/prefabricants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/prefabricants";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PrefabricantRepository prefabricantRepository;

    @Autowired
    private PrefabricantMapper prefabricantMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.PrefabricantSearchRepositoryMockConfiguration
     */
    @Autowired
    private PrefabricantSearchRepository mockPrefabricantSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Prefabricant prefabricant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Prefabricant createEntity(EntityManager em) {
        Prefabricant prefabricant = new Prefabricant().libelle(DEFAULT_LIBELLE);
        return prefabricant;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Prefabricant createUpdatedEntity(EntityManager em) {
        Prefabricant prefabricant = new Prefabricant().libelle(UPDATED_LIBELLE);
        return prefabricant;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Prefabricant.class).block();
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
        prefabricant = createEntity(em);
    }

    @Test
    void createPrefabricant() throws Exception {
        int databaseSizeBeforeCreate = prefabricantRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockPrefabricantSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Prefabricant
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(prefabricant);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll().collectList().block();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeCreate + 1);
        Prefabricant testPrefabricant = prefabricantList.get(prefabricantList.size() - 1);
        assertThat(testPrefabricant.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository, times(1)).save(testPrefabricant);
    }

    @Test
    void createPrefabricantWithExistingId() throws Exception {
        // Create the Prefabricant with an existing ID
        prefabricant.setId(1L);
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(prefabricant);

        int databaseSizeBeforeCreate = prefabricantRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll().collectList().block();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeCreate);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository, times(0)).save(prefabricant);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = prefabricantRepository.findAll().collectList().block().size();
        // set the field null
        prefabricant.setLibelle(null);

        // Create the Prefabricant, which fails.
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(prefabricant);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Prefabricant> prefabricantList = prefabricantRepository.findAll().collectList().block();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllPrefabricants() {
        // Initialize the database
        prefabricantRepository.save(prefabricant).block();

        // Get all the prefabricantList
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
            .value(hasItem(prefabricant.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }

    @Test
    void getPrefabricant() {
        // Initialize the database
        prefabricantRepository.save(prefabricant).block();

        // Get the prefabricant
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, prefabricant.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(prefabricant.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE));
    }

    @Test
    void getNonExistingPrefabricant() {
        // Get the prefabricant
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPrefabricant() throws Exception {
        // Configure the mock search repository
        when(mockPrefabricantSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        prefabricantRepository.save(prefabricant).block();

        int databaseSizeBeforeUpdate = prefabricantRepository.findAll().collectList().block().size();

        // Update the prefabricant
        Prefabricant updatedPrefabricant = prefabricantRepository.findById(prefabricant.getId()).block();
        updatedPrefabricant.libelle(UPDATED_LIBELLE);
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(updatedPrefabricant);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, prefabricantDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll().collectList().block();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeUpdate);
        Prefabricant testPrefabricant = prefabricantList.get(prefabricantList.size() - 1);
        assertThat(testPrefabricant.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository).save(testPrefabricant);
    }

    @Test
    void putNonExistingPrefabricant() throws Exception {
        int databaseSizeBeforeUpdate = prefabricantRepository.findAll().collectList().block().size();
        prefabricant.setId(count.incrementAndGet());

        // Create the Prefabricant
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(prefabricant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, prefabricantDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll().collectList().block();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository, times(0)).save(prefabricant);
    }

    @Test
    void putWithIdMismatchPrefabricant() throws Exception {
        int databaseSizeBeforeUpdate = prefabricantRepository.findAll().collectList().block().size();
        prefabricant.setId(count.incrementAndGet());

        // Create the Prefabricant
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(prefabricant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll().collectList().block();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository, times(0)).save(prefabricant);
    }

    @Test
    void putWithMissingIdPathParamPrefabricant() throws Exception {
        int databaseSizeBeforeUpdate = prefabricantRepository.findAll().collectList().block().size();
        prefabricant.setId(count.incrementAndGet());

        // Create the Prefabricant
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(prefabricant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll().collectList().block();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository, times(0)).save(prefabricant);
    }

    @Test
    void partialUpdatePrefabricantWithPatch() throws Exception {
        // Initialize the database
        prefabricantRepository.save(prefabricant).block();

        int databaseSizeBeforeUpdate = prefabricantRepository.findAll().collectList().block().size();

        // Update the prefabricant using partial update
        Prefabricant partialUpdatedPrefabricant = new Prefabricant();
        partialUpdatedPrefabricant.setId(prefabricant.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPrefabricant.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPrefabricant))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll().collectList().block();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeUpdate);
        Prefabricant testPrefabricant = prefabricantList.get(prefabricantList.size() - 1);
        assertThat(testPrefabricant.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
    }

    @Test
    void fullUpdatePrefabricantWithPatch() throws Exception {
        // Initialize the database
        prefabricantRepository.save(prefabricant).block();

        int databaseSizeBeforeUpdate = prefabricantRepository.findAll().collectList().block().size();

        // Update the prefabricant using partial update
        Prefabricant partialUpdatedPrefabricant = new Prefabricant();
        partialUpdatedPrefabricant.setId(prefabricant.getId());

        partialUpdatedPrefabricant.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPrefabricant.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPrefabricant))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll().collectList().block();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeUpdate);
        Prefabricant testPrefabricant = prefabricantList.get(prefabricantList.size() - 1);
        assertThat(testPrefabricant.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void patchNonExistingPrefabricant() throws Exception {
        int databaseSizeBeforeUpdate = prefabricantRepository.findAll().collectList().block().size();
        prefabricant.setId(count.incrementAndGet());

        // Create the Prefabricant
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(prefabricant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, prefabricantDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll().collectList().block();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository, times(0)).save(prefabricant);
    }

    @Test
    void patchWithIdMismatchPrefabricant() throws Exception {
        int databaseSizeBeforeUpdate = prefabricantRepository.findAll().collectList().block().size();
        prefabricant.setId(count.incrementAndGet());

        // Create the Prefabricant
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(prefabricant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll().collectList().block();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository, times(0)).save(prefabricant);
    }

    @Test
    void patchWithMissingIdPathParamPrefabricant() throws Exception {
        int databaseSizeBeforeUpdate = prefabricantRepository.findAll().collectList().block().size();
        prefabricant.setId(count.incrementAndGet());

        // Create the Prefabricant
        PrefabricantDTO prefabricantDTO = prefabricantMapper.toDto(prefabricant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(prefabricantDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Prefabricant in the database
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll().collectList().block();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository, times(0)).save(prefabricant);
    }

    @Test
    void deletePrefabricant() {
        // Configure the mock search repository
        when(mockPrefabricantSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockPrefabricantSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        prefabricantRepository.save(prefabricant).block();

        int databaseSizeBeforeDelete = prefabricantRepository.findAll().collectList().block().size();

        // Delete the prefabricant
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, prefabricant.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Prefabricant> prefabricantList = prefabricantRepository.findAll().collectList().block();
        assertThat(prefabricantList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Prefabricant in Elasticsearch
        verify(mockPrefabricantSearchRepository, times(1)).deleteById(prefabricant.getId());
    }

    @Test
    void searchPrefabricant() {
        // Configure the mock search repository
        when(mockPrefabricantSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockPrefabricantSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        prefabricantRepository.save(prefabricant).block();
        when(mockPrefabricantSearchRepository.search("id:" + prefabricant.getId(), PageRequest.of(0, 20)))
            .thenReturn(Flux.just(prefabricant));

        // Search the prefabricant
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + prefabricant.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(prefabricant.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }
}
