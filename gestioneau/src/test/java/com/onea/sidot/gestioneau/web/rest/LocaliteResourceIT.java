package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.Localite;
import com.onea.sidot.gestioneau.repository.LocaliteRepository;
import com.onea.sidot.gestioneau.repository.search.LocaliteSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.LocaliteDTO;
import com.onea.sidot.gestioneau.service.mapper.LocaliteMapper;
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
 * Integration tests for the {@link LocaliteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class LocaliteResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/localites";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/localites";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LocaliteRepository localiteRepository;

    @Autowired
    private LocaliteMapper localiteMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.LocaliteSearchRepositoryMockConfiguration
     */
    @Autowired
    private LocaliteSearchRepository mockLocaliteSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Localite localite;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Localite createEntity(EntityManager em) {
        Localite localite = new Localite().libelle(DEFAULT_LIBELLE);
        return localite;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Localite createUpdatedEntity(EntityManager em) {
        Localite localite = new Localite().libelle(UPDATED_LIBELLE);
        return localite;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Localite.class).block();
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
        localite = createEntity(em);
    }

    @Test
    void createLocalite() throws Exception {
        int databaseSizeBeforeCreate = localiteRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockLocaliteSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Localite
        LocaliteDTO localiteDTO = localiteMapper.toDto(localite);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(localiteDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Localite in the database
        List<Localite> localiteList = localiteRepository.findAll().collectList().block();
        assertThat(localiteList).hasSize(databaseSizeBeforeCreate + 1);
        Localite testLocalite = localiteList.get(localiteList.size() - 1);
        assertThat(testLocalite.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the Localite in Elasticsearch
        verify(mockLocaliteSearchRepository, times(1)).save(testLocalite);
    }

    @Test
    void createLocaliteWithExistingId() throws Exception {
        // Create the Localite with an existing ID
        localite.setId(1L);
        LocaliteDTO localiteDTO = localiteMapper.toDto(localite);

        int databaseSizeBeforeCreate = localiteRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(localiteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Localite in the database
        List<Localite> localiteList = localiteRepository.findAll().collectList().block();
        assertThat(localiteList).hasSize(databaseSizeBeforeCreate);

        // Validate the Localite in Elasticsearch
        verify(mockLocaliteSearchRepository, times(0)).save(localite);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = localiteRepository.findAll().collectList().block().size();
        // set the field null
        localite.setLibelle(null);

        // Create the Localite, which fails.
        LocaliteDTO localiteDTO = localiteMapper.toDto(localite);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(localiteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Localite> localiteList = localiteRepository.findAll().collectList().block();
        assertThat(localiteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllLocalites() {
        // Initialize the database
        localiteRepository.save(localite).block();

        // Get all the localiteList
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
            .value(hasItem(localite.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }

    @Test
    void getLocalite() {
        // Initialize the database
        localiteRepository.save(localite).block();

        // Get the localite
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, localite.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(localite.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE));
    }

    @Test
    void getNonExistingLocalite() {
        // Get the localite
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewLocalite() throws Exception {
        // Configure the mock search repository
        when(mockLocaliteSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        localiteRepository.save(localite).block();

        int databaseSizeBeforeUpdate = localiteRepository.findAll().collectList().block().size();

        // Update the localite
        Localite updatedLocalite = localiteRepository.findById(localite.getId()).block();
        updatedLocalite.libelle(UPDATED_LIBELLE);
        LocaliteDTO localiteDTO = localiteMapper.toDto(updatedLocalite);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, localiteDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(localiteDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Localite in the database
        List<Localite> localiteList = localiteRepository.findAll().collectList().block();
        assertThat(localiteList).hasSize(databaseSizeBeforeUpdate);
        Localite testLocalite = localiteList.get(localiteList.size() - 1);
        assertThat(testLocalite.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the Localite in Elasticsearch
        verify(mockLocaliteSearchRepository).save(testLocalite);
    }

    @Test
    void putNonExistingLocalite() throws Exception {
        int databaseSizeBeforeUpdate = localiteRepository.findAll().collectList().block().size();
        localite.setId(count.incrementAndGet());

        // Create the Localite
        LocaliteDTO localiteDTO = localiteMapper.toDto(localite);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, localiteDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(localiteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Localite in the database
        List<Localite> localiteList = localiteRepository.findAll().collectList().block();
        assertThat(localiteList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Localite in Elasticsearch
        verify(mockLocaliteSearchRepository, times(0)).save(localite);
    }

    @Test
    void putWithIdMismatchLocalite() throws Exception {
        int databaseSizeBeforeUpdate = localiteRepository.findAll().collectList().block().size();
        localite.setId(count.incrementAndGet());

        // Create the Localite
        LocaliteDTO localiteDTO = localiteMapper.toDto(localite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(localiteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Localite in the database
        List<Localite> localiteList = localiteRepository.findAll().collectList().block();
        assertThat(localiteList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Localite in Elasticsearch
        verify(mockLocaliteSearchRepository, times(0)).save(localite);
    }

    @Test
    void putWithMissingIdPathParamLocalite() throws Exception {
        int databaseSizeBeforeUpdate = localiteRepository.findAll().collectList().block().size();
        localite.setId(count.incrementAndGet());

        // Create the Localite
        LocaliteDTO localiteDTO = localiteMapper.toDto(localite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(localiteDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Localite in the database
        List<Localite> localiteList = localiteRepository.findAll().collectList().block();
        assertThat(localiteList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Localite in Elasticsearch
        verify(mockLocaliteSearchRepository, times(0)).save(localite);
    }

    @Test
    void partialUpdateLocaliteWithPatch() throws Exception {
        // Initialize the database
        localiteRepository.save(localite).block();

        int databaseSizeBeforeUpdate = localiteRepository.findAll().collectList().block().size();

        // Update the localite using partial update
        Localite partialUpdatedLocalite = new Localite();
        partialUpdatedLocalite.setId(localite.getId());

        partialUpdatedLocalite.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLocalite.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedLocalite))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Localite in the database
        List<Localite> localiteList = localiteRepository.findAll().collectList().block();
        assertThat(localiteList).hasSize(databaseSizeBeforeUpdate);
        Localite testLocalite = localiteList.get(localiteList.size() - 1);
        assertThat(testLocalite.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void fullUpdateLocaliteWithPatch() throws Exception {
        // Initialize the database
        localiteRepository.save(localite).block();

        int databaseSizeBeforeUpdate = localiteRepository.findAll().collectList().block().size();

        // Update the localite using partial update
        Localite partialUpdatedLocalite = new Localite();
        partialUpdatedLocalite.setId(localite.getId());

        partialUpdatedLocalite.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLocalite.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedLocalite))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Localite in the database
        List<Localite> localiteList = localiteRepository.findAll().collectList().block();
        assertThat(localiteList).hasSize(databaseSizeBeforeUpdate);
        Localite testLocalite = localiteList.get(localiteList.size() - 1);
        assertThat(testLocalite.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void patchNonExistingLocalite() throws Exception {
        int databaseSizeBeforeUpdate = localiteRepository.findAll().collectList().block().size();
        localite.setId(count.incrementAndGet());

        // Create the Localite
        LocaliteDTO localiteDTO = localiteMapper.toDto(localite);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, localiteDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(localiteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Localite in the database
        List<Localite> localiteList = localiteRepository.findAll().collectList().block();
        assertThat(localiteList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Localite in Elasticsearch
        verify(mockLocaliteSearchRepository, times(0)).save(localite);
    }

    @Test
    void patchWithIdMismatchLocalite() throws Exception {
        int databaseSizeBeforeUpdate = localiteRepository.findAll().collectList().block().size();
        localite.setId(count.incrementAndGet());

        // Create the Localite
        LocaliteDTO localiteDTO = localiteMapper.toDto(localite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(localiteDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Localite in the database
        List<Localite> localiteList = localiteRepository.findAll().collectList().block();
        assertThat(localiteList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Localite in Elasticsearch
        verify(mockLocaliteSearchRepository, times(0)).save(localite);
    }

    @Test
    void patchWithMissingIdPathParamLocalite() throws Exception {
        int databaseSizeBeforeUpdate = localiteRepository.findAll().collectList().block().size();
        localite.setId(count.incrementAndGet());

        // Create the Localite
        LocaliteDTO localiteDTO = localiteMapper.toDto(localite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(localiteDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Localite in the database
        List<Localite> localiteList = localiteRepository.findAll().collectList().block();
        assertThat(localiteList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Localite in Elasticsearch
        verify(mockLocaliteSearchRepository, times(0)).save(localite);
    }

    @Test
    void deleteLocalite() {
        // Configure the mock search repository
        when(mockLocaliteSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockLocaliteSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        localiteRepository.save(localite).block();

        int databaseSizeBeforeDelete = localiteRepository.findAll().collectList().block().size();

        // Delete the localite
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, localite.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Localite> localiteList = localiteRepository.findAll().collectList().block();
        assertThat(localiteList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Localite in Elasticsearch
        verify(mockLocaliteSearchRepository, times(1)).deleteById(localite.getId());
    }

    @Test
    void searchLocalite() {
        // Configure the mock search repository
        when(mockLocaliteSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockLocaliteSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        localiteRepository.save(localite).block();
        when(mockLocaliteSearchRepository.search("id:" + localite.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(localite));

        // Search the localite
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + localite.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(localite.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }
}
