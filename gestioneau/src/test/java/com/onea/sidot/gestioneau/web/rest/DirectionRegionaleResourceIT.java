package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.DirectionRegionale;
import com.onea.sidot.gestioneau.repository.DirectionRegionaleRepository;
import com.onea.sidot.gestioneau.repository.search.DirectionRegionaleSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.DirectionRegionaleDTO;
import com.onea.sidot.gestioneau.service.mapper.DirectionRegionaleMapper;
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
 * Integration tests for the {@link DirectionRegionaleResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class DirectionRegionaleResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String DEFAULT_RESPONSABLE = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSABLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/direction-regionales";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/direction-regionales";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DirectionRegionaleRepository directionRegionaleRepository;

    @Autowired
    private DirectionRegionaleMapper directionRegionaleMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.DirectionRegionaleSearchRepositoryMockConfiguration
     */
    @Autowired
    private DirectionRegionaleSearchRepository mockDirectionRegionaleSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private DirectionRegionale directionRegionale;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DirectionRegionale createEntity(EntityManager em) {
        DirectionRegionale directionRegionale = new DirectionRegionale()
            .libelle(DEFAULT_LIBELLE)
            .responsable(DEFAULT_RESPONSABLE)
            .contact(DEFAULT_CONTACT);
        return directionRegionale;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DirectionRegionale createUpdatedEntity(EntityManager em) {
        DirectionRegionale directionRegionale = new DirectionRegionale()
            .libelle(UPDATED_LIBELLE)
            .responsable(UPDATED_RESPONSABLE)
            .contact(UPDATED_CONTACT);
        return directionRegionale;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(DirectionRegionale.class).block();
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
        directionRegionale = createEntity(em);
    }

    @Test
    void createDirectionRegionale() throws Exception {
        int databaseSizeBeforeCreate = directionRegionaleRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockDirectionRegionaleSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the DirectionRegionale
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll().collectList().block();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeCreate + 1);
        DirectionRegionale testDirectionRegionale = directionRegionaleList.get(directionRegionaleList.size() - 1);
        assertThat(testDirectionRegionale.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testDirectionRegionale.getResponsable()).isEqualTo(DEFAULT_RESPONSABLE);
        assertThat(testDirectionRegionale.getContact()).isEqualTo(DEFAULT_CONTACT);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository, times(1)).save(testDirectionRegionale);
    }

    @Test
    void createDirectionRegionaleWithExistingId() throws Exception {
        // Create the DirectionRegionale with an existing ID
        directionRegionale.setId(1L);
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        int databaseSizeBeforeCreate = directionRegionaleRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll().collectList().block();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeCreate);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository, times(0)).save(directionRegionale);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = directionRegionaleRepository.findAll().collectList().block().size();
        // set the field null
        directionRegionale.setLibelle(null);

        // Create the DirectionRegionale, which fails.
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll().collectList().block();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkResponsableIsRequired() throws Exception {
        int databaseSizeBeforeTest = directionRegionaleRepository.findAll().collectList().block().size();
        // set the field null
        directionRegionale.setResponsable(null);

        // Create the DirectionRegionale, which fails.
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll().collectList().block();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkContactIsRequired() throws Exception {
        int databaseSizeBeforeTest = directionRegionaleRepository.findAll().collectList().block().size();
        // set the field null
        directionRegionale.setContact(null);

        // Create the DirectionRegionale, which fails.
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll().collectList().block();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllDirectionRegionales() {
        // Initialize the database
        directionRegionaleRepository.save(directionRegionale).block();

        // Get all the directionRegionaleList
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
            .value(hasItem(directionRegionale.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE))
            .jsonPath("$.[*].responsable")
            .value(hasItem(DEFAULT_RESPONSABLE))
            .jsonPath("$.[*].contact")
            .value(hasItem(DEFAULT_CONTACT));
    }

    @Test
    void getDirectionRegionale() {
        // Initialize the database
        directionRegionaleRepository.save(directionRegionale).block();

        // Get the directionRegionale
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, directionRegionale.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(directionRegionale.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE))
            .jsonPath("$.responsable")
            .value(is(DEFAULT_RESPONSABLE))
            .jsonPath("$.contact")
            .value(is(DEFAULT_CONTACT));
    }

    @Test
    void getNonExistingDirectionRegionale() {
        // Get the directionRegionale
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewDirectionRegionale() throws Exception {
        // Configure the mock search repository
        when(mockDirectionRegionaleSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        directionRegionaleRepository.save(directionRegionale).block();

        int databaseSizeBeforeUpdate = directionRegionaleRepository.findAll().collectList().block().size();

        // Update the directionRegionale
        DirectionRegionale updatedDirectionRegionale = directionRegionaleRepository.findById(directionRegionale.getId()).block();
        updatedDirectionRegionale.libelle(UPDATED_LIBELLE).responsable(UPDATED_RESPONSABLE).contact(UPDATED_CONTACT);
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(updatedDirectionRegionale);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, directionRegionaleDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll().collectList().block();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeUpdate);
        DirectionRegionale testDirectionRegionale = directionRegionaleList.get(directionRegionaleList.size() - 1);
        assertThat(testDirectionRegionale.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testDirectionRegionale.getResponsable()).isEqualTo(UPDATED_RESPONSABLE);
        assertThat(testDirectionRegionale.getContact()).isEqualTo(UPDATED_CONTACT);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository).save(testDirectionRegionale);
    }

    @Test
    void putNonExistingDirectionRegionale() throws Exception {
        int databaseSizeBeforeUpdate = directionRegionaleRepository.findAll().collectList().block().size();
        directionRegionale.setId(count.incrementAndGet());

        // Create the DirectionRegionale
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, directionRegionaleDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll().collectList().block();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository, times(0)).save(directionRegionale);
    }

    @Test
    void putWithIdMismatchDirectionRegionale() throws Exception {
        int databaseSizeBeforeUpdate = directionRegionaleRepository.findAll().collectList().block().size();
        directionRegionale.setId(count.incrementAndGet());

        // Create the DirectionRegionale
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll().collectList().block();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository, times(0)).save(directionRegionale);
    }

    @Test
    void putWithMissingIdPathParamDirectionRegionale() throws Exception {
        int databaseSizeBeforeUpdate = directionRegionaleRepository.findAll().collectList().block().size();
        directionRegionale.setId(count.incrementAndGet());

        // Create the DirectionRegionale
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll().collectList().block();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository, times(0)).save(directionRegionale);
    }

    @Test
    void partialUpdateDirectionRegionaleWithPatch() throws Exception {
        // Initialize the database
        directionRegionaleRepository.save(directionRegionale).block();

        int databaseSizeBeforeUpdate = directionRegionaleRepository.findAll().collectList().block().size();

        // Update the directionRegionale using partial update
        DirectionRegionale partialUpdatedDirectionRegionale = new DirectionRegionale();
        partialUpdatedDirectionRegionale.setId(directionRegionale.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDirectionRegionale.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDirectionRegionale))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll().collectList().block();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeUpdate);
        DirectionRegionale testDirectionRegionale = directionRegionaleList.get(directionRegionaleList.size() - 1);
        assertThat(testDirectionRegionale.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testDirectionRegionale.getResponsable()).isEqualTo(DEFAULT_RESPONSABLE);
        assertThat(testDirectionRegionale.getContact()).isEqualTo(DEFAULT_CONTACT);
    }

    @Test
    void fullUpdateDirectionRegionaleWithPatch() throws Exception {
        // Initialize the database
        directionRegionaleRepository.save(directionRegionale).block();

        int databaseSizeBeforeUpdate = directionRegionaleRepository.findAll().collectList().block().size();

        // Update the directionRegionale using partial update
        DirectionRegionale partialUpdatedDirectionRegionale = new DirectionRegionale();
        partialUpdatedDirectionRegionale.setId(directionRegionale.getId());

        partialUpdatedDirectionRegionale.libelle(UPDATED_LIBELLE).responsable(UPDATED_RESPONSABLE).contact(UPDATED_CONTACT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDirectionRegionale.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedDirectionRegionale))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll().collectList().block();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeUpdate);
        DirectionRegionale testDirectionRegionale = directionRegionaleList.get(directionRegionaleList.size() - 1);
        assertThat(testDirectionRegionale.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testDirectionRegionale.getResponsable()).isEqualTo(UPDATED_RESPONSABLE);
        assertThat(testDirectionRegionale.getContact()).isEqualTo(UPDATED_CONTACT);
    }

    @Test
    void patchNonExistingDirectionRegionale() throws Exception {
        int databaseSizeBeforeUpdate = directionRegionaleRepository.findAll().collectList().block().size();
        directionRegionale.setId(count.incrementAndGet());

        // Create the DirectionRegionale
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, directionRegionaleDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll().collectList().block();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository, times(0)).save(directionRegionale);
    }

    @Test
    void patchWithIdMismatchDirectionRegionale() throws Exception {
        int databaseSizeBeforeUpdate = directionRegionaleRepository.findAll().collectList().block().size();
        directionRegionale.setId(count.incrementAndGet());

        // Create the DirectionRegionale
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll().collectList().block();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository, times(0)).save(directionRegionale);
    }

    @Test
    void patchWithMissingIdPathParamDirectionRegionale() throws Exception {
        int databaseSizeBeforeUpdate = directionRegionaleRepository.findAll().collectList().block().size();
        directionRegionale.setId(count.incrementAndGet());

        // Create the DirectionRegionale
        DirectionRegionaleDTO directionRegionaleDTO = directionRegionaleMapper.toDto(directionRegionale);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(directionRegionaleDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DirectionRegionale in the database
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll().collectList().block();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository, times(0)).save(directionRegionale);
    }

    @Test
    void deleteDirectionRegionale() {
        // Configure the mock search repository
        when(mockDirectionRegionaleSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockDirectionRegionaleSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        directionRegionaleRepository.save(directionRegionale).block();

        int databaseSizeBeforeDelete = directionRegionaleRepository.findAll().collectList().block().size();

        // Delete the directionRegionale
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, directionRegionale.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<DirectionRegionale> directionRegionaleList = directionRegionaleRepository.findAll().collectList().block();
        assertThat(directionRegionaleList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the DirectionRegionale in Elasticsearch
        verify(mockDirectionRegionaleSearchRepository, times(1)).deleteById(directionRegionale.getId());
    }

    @Test
    void searchDirectionRegionale() {
        // Configure the mock search repository
        when(mockDirectionRegionaleSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockDirectionRegionaleSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        directionRegionaleRepository.save(directionRegionale).block();
        when(mockDirectionRegionaleSearchRepository.search("id:" + directionRegionale.getId(), PageRequest.of(0, 20)))
            .thenReturn(Flux.just(directionRegionale));

        // Search the directionRegionale
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + directionRegionale.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(directionRegionale.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE))
            .jsonPath("$.[*].responsable")
            .value(hasItem(DEFAULT_RESPONSABLE))
            .jsonPath("$.[*].contact")
            .value(hasItem(DEFAULT_CONTACT));
    }
}
