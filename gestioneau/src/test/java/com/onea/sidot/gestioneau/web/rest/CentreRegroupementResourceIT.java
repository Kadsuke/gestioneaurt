package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.CentreRegroupement;
import com.onea.sidot.gestioneau.repository.CentreRegroupementRepository;
import com.onea.sidot.gestioneau.repository.search.CentreRegroupementSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.CentreRegroupementDTO;
import com.onea.sidot.gestioneau.service.mapper.CentreRegroupementMapper;
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
 * Integration tests for the {@link CentreRegroupementResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class CentreRegroupementResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String DEFAULT_RESPONSABLE = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSABLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/centre-regroupements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/centre-regroupements";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CentreRegroupementRepository centreRegroupementRepository;

    @Autowired
    private CentreRegroupementMapper centreRegroupementMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.CentreRegroupementSearchRepositoryMockConfiguration
     */
    @Autowired
    private CentreRegroupementSearchRepository mockCentreRegroupementSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private CentreRegroupement centreRegroupement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CentreRegroupement createEntity(EntityManager em) {
        CentreRegroupement centreRegroupement = new CentreRegroupement()
            .libelle(DEFAULT_LIBELLE)
            .responsable(DEFAULT_RESPONSABLE)
            .contact(DEFAULT_CONTACT);
        return centreRegroupement;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CentreRegroupement createUpdatedEntity(EntityManager em) {
        CentreRegroupement centreRegroupement = new CentreRegroupement()
            .libelle(UPDATED_LIBELLE)
            .responsable(UPDATED_RESPONSABLE)
            .contact(UPDATED_CONTACT);
        return centreRegroupement;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(CentreRegroupement.class).block();
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
        centreRegroupement = createEntity(em);
    }

    @Test
    void createCentreRegroupement() throws Exception {
        int databaseSizeBeforeCreate = centreRegroupementRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockCentreRegroupementSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the CentreRegroupement
        CentreRegroupementDTO centreRegroupementDTO = centreRegroupementMapper.toDto(centreRegroupement);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreRegroupementDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the CentreRegroupement in the database
        List<CentreRegroupement> centreRegroupementList = centreRegroupementRepository.findAll().collectList().block();
        assertThat(centreRegroupementList).hasSize(databaseSizeBeforeCreate + 1);
        CentreRegroupement testCentreRegroupement = centreRegroupementList.get(centreRegroupementList.size() - 1);
        assertThat(testCentreRegroupement.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testCentreRegroupement.getResponsable()).isEqualTo(DEFAULT_RESPONSABLE);
        assertThat(testCentreRegroupement.getContact()).isEqualTo(DEFAULT_CONTACT);

        // Validate the CentreRegroupement in Elasticsearch
        verify(mockCentreRegroupementSearchRepository, times(1)).save(testCentreRegroupement);
    }

    @Test
    void createCentreRegroupementWithExistingId() throws Exception {
        // Create the CentreRegroupement with an existing ID
        centreRegroupement.setId(1L);
        CentreRegroupementDTO centreRegroupementDTO = centreRegroupementMapper.toDto(centreRegroupement);

        int databaseSizeBeforeCreate = centreRegroupementRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreRegroupementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CentreRegroupement in the database
        List<CentreRegroupement> centreRegroupementList = centreRegroupementRepository.findAll().collectList().block();
        assertThat(centreRegroupementList).hasSize(databaseSizeBeforeCreate);

        // Validate the CentreRegroupement in Elasticsearch
        verify(mockCentreRegroupementSearchRepository, times(0)).save(centreRegroupement);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = centreRegroupementRepository.findAll().collectList().block().size();
        // set the field null
        centreRegroupement.setLibelle(null);

        // Create the CentreRegroupement, which fails.
        CentreRegroupementDTO centreRegroupementDTO = centreRegroupementMapper.toDto(centreRegroupement);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreRegroupementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CentreRegroupement> centreRegroupementList = centreRegroupementRepository.findAll().collectList().block();
        assertThat(centreRegroupementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkResponsableIsRequired() throws Exception {
        int databaseSizeBeforeTest = centreRegroupementRepository.findAll().collectList().block().size();
        // set the field null
        centreRegroupement.setResponsable(null);

        // Create the CentreRegroupement, which fails.
        CentreRegroupementDTO centreRegroupementDTO = centreRegroupementMapper.toDto(centreRegroupement);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreRegroupementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CentreRegroupement> centreRegroupementList = centreRegroupementRepository.findAll().collectList().block();
        assertThat(centreRegroupementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkContactIsRequired() throws Exception {
        int databaseSizeBeforeTest = centreRegroupementRepository.findAll().collectList().block().size();
        // set the field null
        centreRegroupement.setContact(null);

        // Create the CentreRegroupement, which fails.
        CentreRegroupementDTO centreRegroupementDTO = centreRegroupementMapper.toDto(centreRegroupement);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreRegroupementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CentreRegroupement> centreRegroupementList = centreRegroupementRepository.findAll().collectList().block();
        assertThat(centreRegroupementList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCentreRegroupements() {
        // Initialize the database
        centreRegroupementRepository.save(centreRegroupement).block();

        // Get all the centreRegroupementList
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
            .value(hasItem(centreRegroupement.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE))
            .jsonPath("$.[*].responsable")
            .value(hasItem(DEFAULT_RESPONSABLE))
            .jsonPath("$.[*].contact")
            .value(hasItem(DEFAULT_CONTACT));
    }

    @Test
    void getCentreRegroupement() {
        // Initialize the database
        centreRegroupementRepository.save(centreRegroupement).block();

        // Get the centreRegroupement
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, centreRegroupement.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(centreRegroupement.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE))
            .jsonPath("$.responsable")
            .value(is(DEFAULT_RESPONSABLE))
            .jsonPath("$.contact")
            .value(is(DEFAULT_CONTACT));
    }

    @Test
    void getNonExistingCentreRegroupement() {
        // Get the centreRegroupement
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCentreRegroupement() throws Exception {
        // Configure the mock search repository
        when(mockCentreRegroupementSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        centreRegroupementRepository.save(centreRegroupement).block();

        int databaseSizeBeforeUpdate = centreRegroupementRepository.findAll().collectList().block().size();

        // Update the centreRegroupement
        CentreRegroupement updatedCentreRegroupement = centreRegroupementRepository.findById(centreRegroupement.getId()).block();
        updatedCentreRegroupement.libelle(UPDATED_LIBELLE).responsable(UPDATED_RESPONSABLE).contact(UPDATED_CONTACT);
        CentreRegroupementDTO centreRegroupementDTO = centreRegroupementMapper.toDto(updatedCentreRegroupement);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, centreRegroupementDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreRegroupementDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CentreRegroupement in the database
        List<CentreRegroupement> centreRegroupementList = centreRegroupementRepository.findAll().collectList().block();
        assertThat(centreRegroupementList).hasSize(databaseSizeBeforeUpdate);
        CentreRegroupement testCentreRegroupement = centreRegroupementList.get(centreRegroupementList.size() - 1);
        assertThat(testCentreRegroupement.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testCentreRegroupement.getResponsable()).isEqualTo(UPDATED_RESPONSABLE);
        assertThat(testCentreRegroupement.getContact()).isEqualTo(UPDATED_CONTACT);

        // Validate the CentreRegroupement in Elasticsearch
        verify(mockCentreRegroupementSearchRepository).save(testCentreRegroupement);
    }

    @Test
    void putNonExistingCentreRegroupement() throws Exception {
        int databaseSizeBeforeUpdate = centreRegroupementRepository.findAll().collectList().block().size();
        centreRegroupement.setId(count.incrementAndGet());

        // Create the CentreRegroupement
        CentreRegroupementDTO centreRegroupementDTO = centreRegroupementMapper.toDto(centreRegroupement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, centreRegroupementDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreRegroupementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CentreRegroupement in the database
        List<CentreRegroupement> centreRegroupementList = centreRegroupementRepository.findAll().collectList().block();
        assertThat(centreRegroupementList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CentreRegroupement in Elasticsearch
        verify(mockCentreRegroupementSearchRepository, times(0)).save(centreRegroupement);
    }

    @Test
    void putWithIdMismatchCentreRegroupement() throws Exception {
        int databaseSizeBeforeUpdate = centreRegroupementRepository.findAll().collectList().block().size();
        centreRegroupement.setId(count.incrementAndGet());

        // Create the CentreRegroupement
        CentreRegroupementDTO centreRegroupementDTO = centreRegroupementMapper.toDto(centreRegroupement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreRegroupementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CentreRegroupement in the database
        List<CentreRegroupement> centreRegroupementList = centreRegroupementRepository.findAll().collectList().block();
        assertThat(centreRegroupementList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CentreRegroupement in Elasticsearch
        verify(mockCentreRegroupementSearchRepository, times(0)).save(centreRegroupement);
    }

    @Test
    void putWithMissingIdPathParamCentreRegroupement() throws Exception {
        int databaseSizeBeforeUpdate = centreRegroupementRepository.findAll().collectList().block().size();
        centreRegroupement.setId(count.incrementAndGet());

        // Create the CentreRegroupement
        CentreRegroupementDTO centreRegroupementDTO = centreRegroupementMapper.toDto(centreRegroupement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreRegroupementDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CentreRegroupement in the database
        List<CentreRegroupement> centreRegroupementList = centreRegroupementRepository.findAll().collectList().block();
        assertThat(centreRegroupementList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CentreRegroupement in Elasticsearch
        verify(mockCentreRegroupementSearchRepository, times(0)).save(centreRegroupement);
    }

    @Test
    void partialUpdateCentreRegroupementWithPatch() throws Exception {
        // Initialize the database
        centreRegroupementRepository.save(centreRegroupement).block();

        int databaseSizeBeforeUpdate = centreRegroupementRepository.findAll().collectList().block().size();

        // Update the centreRegroupement using partial update
        CentreRegroupement partialUpdatedCentreRegroupement = new CentreRegroupement();
        partialUpdatedCentreRegroupement.setId(centreRegroupement.getId());

        partialUpdatedCentreRegroupement.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCentreRegroupement.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCentreRegroupement))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CentreRegroupement in the database
        List<CentreRegroupement> centreRegroupementList = centreRegroupementRepository.findAll().collectList().block();
        assertThat(centreRegroupementList).hasSize(databaseSizeBeforeUpdate);
        CentreRegroupement testCentreRegroupement = centreRegroupementList.get(centreRegroupementList.size() - 1);
        assertThat(testCentreRegroupement.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testCentreRegroupement.getResponsable()).isEqualTo(DEFAULT_RESPONSABLE);
        assertThat(testCentreRegroupement.getContact()).isEqualTo(DEFAULT_CONTACT);
    }

    @Test
    void fullUpdateCentreRegroupementWithPatch() throws Exception {
        // Initialize the database
        centreRegroupementRepository.save(centreRegroupement).block();

        int databaseSizeBeforeUpdate = centreRegroupementRepository.findAll().collectList().block().size();

        // Update the centreRegroupement using partial update
        CentreRegroupement partialUpdatedCentreRegroupement = new CentreRegroupement();
        partialUpdatedCentreRegroupement.setId(centreRegroupement.getId());

        partialUpdatedCentreRegroupement.libelle(UPDATED_LIBELLE).responsable(UPDATED_RESPONSABLE).contact(UPDATED_CONTACT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCentreRegroupement.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCentreRegroupement))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CentreRegroupement in the database
        List<CentreRegroupement> centreRegroupementList = centreRegroupementRepository.findAll().collectList().block();
        assertThat(centreRegroupementList).hasSize(databaseSizeBeforeUpdate);
        CentreRegroupement testCentreRegroupement = centreRegroupementList.get(centreRegroupementList.size() - 1);
        assertThat(testCentreRegroupement.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testCentreRegroupement.getResponsable()).isEqualTo(UPDATED_RESPONSABLE);
        assertThat(testCentreRegroupement.getContact()).isEqualTo(UPDATED_CONTACT);
    }

    @Test
    void patchNonExistingCentreRegroupement() throws Exception {
        int databaseSizeBeforeUpdate = centreRegroupementRepository.findAll().collectList().block().size();
        centreRegroupement.setId(count.incrementAndGet());

        // Create the CentreRegroupement
        CentreRegroupementDTO centreRegroupementDTO = centreRegroupementMapper.toDto(centreRegroupement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, centreRegroupementDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreRegroupementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CentreRegroupement in the database
        List<CentreRegroupement> centreRegroupementList = centreRegroupementRepository.findAll().collectList().block();
        assertThat(centreRegroupementList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CentreRegroupement in Elasticsearch
        verify(mockCentreRegroupementSearchRepository, times(0)).save(centreRegroupement);
    }

    @Test
    void patchWithIdMismatchCentreRegroupement() throws Exception {
        int databaseSizeBeforeUpdate = centreRegroupementRepository.findAll().collectList().block().size();
        centreRegroupement.setId(count.incrementAndGet());

        // Create the CentreRegroupement
        CentreRegroupementDTO centreRegroupementDTO = centreRegroupementMapper.toDto(centreRegroupement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreRegroupementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CentreRegroupement in the database
        List<CentreRegroupement> centreRegroupementList = centreRegroupementRepository.findAll().collectList().block();
        assertThat(centreRegroupementList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CentreRegroupement in Elasticsearch
        verify(mockCentreRegroupementSearchRepository, times(0)).save(centreRegroupement);
    }

    @Test
    void patchWithMissingIdPathParamCentreRegroupement() throws Exception {
        int databaseSizeBeforeUpdate = centreRegroupementRepository.findAll().collectList().block().size();
        centreRegroupement.setId(count.incrementAndGet());

        // Create the CentreRegroupement
        CentreRegroupementDTO centreRegroupementDTO = centreRegroupementMapper.toDto(centreRegroupement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreRegroupementDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CentreRegroupement in the database
        List<CentreRegroupement> centreRegroupementList = centreRegroupementRepository.findAll().collectList().block();
        assertThat(centreRegroupementList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CentreRegroupement in Elasticsearch
        verify(mockCentreRegroupementSearchRepository, times(0)).save(centreRegroupement);
    }

    @Test
    void deleteCentreRegroupement() {
        // Configure the mock search repository
        when(mockCentreRegroupementSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockCentreRegroupementSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        centreRegroupementRepository.save(centreRegroupement).block();

        int databaseSizeBeforeDelete = centreRegroupementRepository.findAll().collectList().block().size();

        // Delete the centreRegroupement
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, centreRegroupement.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<CentreRegroupement> centreRegroupementList = centreRegroupementRepository.findAll().collectList().block();
        assertThat(centreRegroupementList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the CentreRegroupement in Elasticsearch
        verify(mockCentreRegroupementSearchRepository, times(1)).deleteById(centreRegroupement.getId());
    }

    @Test
    void searchCentreRegroupement() {
        // Configure the mock search repository
        when(mockCentreRegroupementSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockCentreRegroupementSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        centreRegroupementRepository.save(centreRegroupement).block();
        when(mockCentreRegroupementSearchRepository.search("id:" + centreRegroupement.getId(), PageRequest.of(0, 20)))
            .thenReturn(Flux.just(centreRegroupement));

        // Search the centreRegroupement
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + centreRegroupement.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(centreRegroupement.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE))
            .jsonPath("$.[*].responsable")
            .value(hasItem(DEFAULT_RESPONSABLE))
            .jsonPath("$.[*].contact")
            .value(hasItem(DEFAULT_CONTACT));
    }
}
