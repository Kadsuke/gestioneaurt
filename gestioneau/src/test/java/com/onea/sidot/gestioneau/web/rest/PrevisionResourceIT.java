package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.Prevision;
import com.onea.sidot.gestioneau.repository.PrevisionRepository;
import com.onea.sidot.gestioneau.repository.search.PrevisionSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.PrevisionDTO;
import com.onea.sidot.gestioneau.service.mapper.PrevisionMapper;
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
 * Integration tests for the {@link PrevisionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class PrevisionResourceIT {

    private static final Integer DEFAULT_NB_LATRINE = 1;
    private static final Integer UPDATED_NB_LATRINE = 2;

    private static final Integer DEFAULT_NB_PUISARD = 1;
    private static final Integer UPDATED_NB_PUISARD = 2;

    private static final Integer DEFAULT_NB_PUBLIC = 1;
    private static final Integer UPDATED_NB_PUBLIC = 2;

    private static final Integer DEFAULT_NB_SCOLAIRE = 1;
    private static final Integer UPDATED_NB_SCOLAIRE = 2;

    private static final String ENTITY_API_URL = "/api/previsions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/previsions";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PrevisionRepository previsionRepository;

    @Autowired
    private PrevisionMapper previsionMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.PrevisionSearchRepositoryMockConfiguration
     */
    @Autowired
    private PrevisionSearchRepository mockPrevisionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Prevision prevision;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Prevision createEntity(EntityManager em) {
        Prevision prevision = new Prevision()
            .nbLatrine(DEFAULT_NB_LATRINE)
            .nbPuisard(DEFAULT_NB_PUISARD)
            .nbPublic(DEFAULT_NB_PUBLIC)
            .nbScolaire(DEFAULT_NB_SCOLAIRE);
        return prevision;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Prevision createUpdatedEntity(EntityManager em) {
        Prevision prevision = new Prevision()
            .nbLatrine(UPDATED_NB_LATRINE)
            .nbPuisard(UPDATED_NB_PUISARD)
            .nbPublic(UPDATED_NB_PUBLIC)
            .nbScolaire(UPDATED_NB_SCOLAIRE);
        return prevision;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Prevision.class).block();
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
        prevision = createEntity(em);
    }

    @Test
    void createPrevision() throws Exception {
        int databaseSizeBeforeCreate = previsionRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockPrevisionSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Prevision
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(previsionDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll().collectList().block();
        assertThat(previsionList).hasSize(databaseSizeBeforeCreate + 1);
        Prevision testPrevision = previsionList.get(previsionList.size() - 1);
        assertThat(testPrevision.getNbLatrine()).isEqualTo(DEFAULT_NB_LATRINE);
        assertThat(testPrevision.getNbPuisard()).isEqualTo(DEFAULT_NB_PUISARD);
        assertThat(testPrevision.getNbPublic()).isEqualTo(DEFAULT_NB_PUBLIC);
        assertThat(testPrevision.getNbScolaire()).isEqualTo(DEFAULT_NB_SCOLAIRE);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository, times(1)).save(testPrevision);
    }

    @Test
    void createPrevisionWithExistingId() throws Exception {
        // Create the Prevision with an existing ID
        prevision.setId(1L);
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        int databaseSizeBeforeCreate = previsionRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(previsionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll().collectList().block();
        assertThat(previsionList).hasSize(databaseSizeBeforeCreate);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository, times(0)).save(prevision);
    }

    @Test
    void checkNbLatrineIsRequired() throws Exception {
        int databaseSizeBeforeTest = previsionRepository.findAll().collectList().block().size();
        // set the field null
        prevision.setNbLatrine(null);

        // Create the Prevision, which fails.
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(previsionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Prevision> previsionList = previsionRepository.findAll().collectList().block();
        assertThat(previsionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkNbPuisardIsRequired() throws Exception {
        int databaseSizeBeforeTest = previsionRepository.findAll().collectList().block().size();
        // set the field null
        prevision.setNbPuisard(null);

        // Create the Prevision, which fails.
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(previsionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Prevision> previsionList = previsionRepository.findAll().collectList().block();
        assertThat(previsionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkNbPublicIsRequired() throws Exception {
        int databaseSizeBeforeTest = previsionRepository.findAll().collectList().block().size();
        // set the field null
        prevision.setNbPublic(null);

        // Create the Prevision, which fails.
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(previsionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Prevision> previsionList = previsionRepository.findAll().collectList().block();
        assertThat(previsionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkNbScolaireIsRequired() throws Exception {
        int databaseSizeBeforeTest = previsionRepository.findAll().collectList().block().size();
        // set the field null
        prevision.setNbScolaire(null);

        // Create the Prevision, which fails.
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(previsionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Prevision> previsionList = previsionRepository.findAll().collectList().block();
        assertThat(previsionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllPrevisions() {
        // Initialize the database
        previsionRepository.save(prevision).block();

        // Get all the previsionList
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
            .value(hasItem(prevision.getId().intValue()))
            .jsonPath("$.[*].nbLatrine")
            .value(hasItem(DEFAULT_NB_LATRINE))
            .jsonPath("$.[*].nbPuisard")
            .value(hasItem(DEFAULT_NB_PUISARD))
            .jsonPath("$.[*].nbPublic")
            .value(hasItem(DEFAULT_NB_PUBLIC))
            .jsonPath("$.[*].nbScolaire")
            .value(hasItem(DEFAULT_NB_SCOLAIRE));
    }

    @Test
    void getPrevision() {
        // Initialize the database
        previsionRepository.save(prevision).block();

        // Get the prevision
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, prevision.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(prevision.getId().intValue()))
            .jsonPath("$.nbLatrine")
            .value(is(DEFAULT_NB_LATRINE))
            .jsonPath("$.nbPuisard")
            .value(is(DEFAULT_NB_PUISARD))
            .jsonPath("$.nbPublic")
            .value(is(DEFAULT_NB_PUBLIC))
            .jsonPath("$.nbScolaire")
            .value(is(DEFAULT_NB_SCOLAIRE));
    }

    @Test
    void getNonExistingPrevision() {
        // Get the prevision
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPrevision() throws Exception {
        // Configure the mock search repository
        when(mockPrevisionSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        previsionRepository.save(prevision).block();

        int databaseSizeBeforeUpdate = previsionRepository.findAll().collectList().block().size();

        // Update the prevision
        Prevision updatedPrevision = previsionRepository.findById(prevision.getId()).block();
        updatedPrevision
            .nbLatrine(UPDATED_NB_LATRINE)
            .nbPuisard(UPDATED_NB_PUISARD)
            .nbPublic(UPDATED_NB_PUBLIC)
            .nbScolaire(UPDATED_NB_SCOLAIRE);
        PrevisionDTO previsionDTO = previsionMapper.toDto(updatedPrevision);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, previsionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(previsionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll().collectList().block();
        assertThat(previsionList).hasSize(databaseSizeBeforeUpdate);
        Prevision testPrevision = previsionList.get(previsionList.size() - 1);
        assertThat(testPrevision.getNbLatrine()).isEqualTo(UPDATED_NB_LATRINE);
        assertThat(testPrevision.getNbPuisard()).isEqualTo(UPDATED_NB_PUISARD);
        assertThat(testPrevision.getNbPublic()).isEqualTo(UPDATED_NB_PUBLIC);
        assertThat(testPrevision.getNbScolaire()).isEqualTo(UPDATED_NB_SCOLAIRE);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository).save(testPrevision);
    }

    @Test
    void putNonExistingPrevision() throws Exception {
        int databaseSizeBeforeUpdate = previsionRepository.findAll().collectList().block().size();
        prevision.setId(count.incrementAndGet());

        // Create the Prevision
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, previsionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(previsionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll().collectList().block();
        assertThat(previsionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository, times(0)).save(prevision);
    }

    @Test
    void putWithIdMismatchPrevision() throws Exception {
        int databaseSizeBeforeUpdate = previsionRepository.findAll().collectList().block().size();
        prevision.setId(count.incrementAndGet());

        // Create the Prevision
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(previsionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll().collectList().block();
        assertThat(previsionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository, times(0)).save(prevision);
    }

    @Test
    void putWithMissingIdPathParamPrevision() throws Exception {
        int databaseSizeBeforeUpdate = previsionRepository.findAll().collectList().block().size();
        prevision.setId(count.incrementAndGet());

        // Create the Prevision
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(previsionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll().collectList().block();
        assertThat(previsionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository, times(0)).save(prevision);
    }

    @Test
    void partialUpdatePrevisionWithPatch() throws Exception {
        // Initialize the database
        previsionRepository.save(prevision).block();

        int databaseSizeBeforeUpdate = previsionRepository.findAll().collectList().block().size();

        // Update the prevision using partial update
        Prevision partialUpdatedPrevision = new Prevision();
        partialUpdatedPrevision.setId(prevision.getId());

        partialUpdatedPrevision
            .nbLatrine(UPDATED_NB_LATRINE)
            .nbPuisard(UPDATED_NB_PUISARD)
            .nbPublic(UPDATED_NB_PUBLIC)
            .nbScolaire(UPDATED_NB_SCOLAIRE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPrevision.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPrevision))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll().collectList().block();
        assertThat(previsionList).hasSize(databaseSizeBeforeUpdate);
        Prevision testPrevision = previsionList.get(previsionList.size() - 1);
        assertThat(testPrevision.getNbLatrine()).isEqualTo(UPDATED_NB_LATRINE);
        assertThat(testPrevision.getNbPuisard()).isEqualTo(UPDATED_NB_PUISARD);
        assertThat(testPrevision.getNbPublic()).isEqualTo(UPDATED_NB_PUBLIC);
        assertThat(testPrevision.getNbScolaire()).isEqualTo(UPDATED_NB_SCOLAIRE);
    }

    @Test
    void fullUpdatePrevisionWithPatch() throws Exception {
        // Initialize the database
        previsionRepository.save(prevision).block();

        int databaseSizeBeforeUpdate = previsionRepository.findAll().collectList().block().size();

        // Update the prevision using partial update
        Prevision partialUpdatedPrevision = new Prevision();
        partialUpdatedPrevision.setId(prevision.getId());

        partialUpdatedPrevision
            .nbLatrine(UPDATED_NB_LATRINE)
            .nbPuisard(UPDATED_NB_PUISARD)
            .nbPublic(UPDATED_NB_PUBLIC)
            .nbScolaire(UPDATED_NB_SCOLAIRE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPrevision.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPrevision))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll().collectList().block();
        assertThat(previsionList).hasSize(databaseSizeBeforeUpdate);
        Prevision testPrevision = previsionList.get(previsionList.size() - 1);
        assertThat(testPrevision.getNbLatrine()).isEqualTo(UPDATED_NB_LATRINE);
        assertThat(testPrevision.getNbPuisard()).isEqualTo(UPDATED_NB_PUISARD);
        assertThat(testPrevision.getNbPublic()).isEqualTo(UPDATED_NB_PUBLIC);
        assertThat(testPrevision.getNbScolaire()).isEqualTo(UPDATED_NB_SCOLAIRE);
    }

    @Test
    void patchNonExistingPrevision() throws Exception {
        int databaseSizeBeforeUpdate = previsionRepository.findAll().collectList().block().size();
        prevision.setId(count.incrementAndGet());

        // Create the Prevision
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, previsionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(previsionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll().collectList().block();
        assertThat(previsionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository, times(0)).save(prevision);
    }

    @Test
    void patchWithIdMismatchPrevision() throws Exception {
        int databaseSizeBeforeUpdate = previsionRepository.findAll().collectList().block().size();
        prevision.setId(count.incrementAndGet());

        // Create the Prevision
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(previsionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll().collectList().block();
        assertThat(previsionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository, times(0)).save(prevision);
    }

    @Test
    void patchWithMissingIdPathParamPrevision() throws Exception {
        int databaseSizeBeforeUpdate = previsionRepository.findAll().collectList().block().size();
        prevision.setId(count.incrementAndGet());

        // Create the Prevision
        PrevisionDTO previsionDTO = previsionMapper.toDto(prevision);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(previsionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Prevision in the database
        List<Prevision> previsionList = previsionRepository.findAll().collectList().block();
        assertThat(previsionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository, times(0)).save(prevision);
    }

    @Test
    void deletePrevision() {
        // Configure the mock search repository
        when(mockPrevisionSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockPrevisionSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        previsionRepository.save(prevision).block();

        int databaseSizeBeforeDelete = previsionRepository.findAll().collectList().block().size();

        // Delete the prevision
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, prevision.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Prevision> previsionList = previsionRepository.findAll().collectList().block();
        assertThat(previsionList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Prevision in Elasticsearch
        verify(mockPrevisionSearchRepository, times(1)).deleteById(prevision.getId());
    }

    @Test
    void searchPrevision() {
        // Configure the mock search repository
        when(mockPrevisionSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockPrevisionSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        previsionRepository.save(prevision).block();
        when(mockPrevisionSearchRepository.search("id:" + prevision.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(prevision));

        // Search the prevision
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + prevision.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(prevision.getId().intValue()))
            .jsonPath("$.[*].nbLatrine")
            .value(hasItem(DEFAULT_NB_LATRINE))
            .jsonPath("$.[*].nbPuisard")
            .value(hasItem(DEFAULT_NB_PUISARD))
            .jsonPath("$.[*].nbPublic")
            .value(hasItem(DEFAULT_NB_PUBLIC))
            .jsonPath("$.[*].nbScolaire")
            .value(hasItem(DEFAULT_NB_SCOLAIRE));
    }
}
