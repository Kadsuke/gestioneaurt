package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.Parcelle;
import com.onea.sidot.gestioneau.repository.ParcelleRepository;
import com.onea.sidot.gestioneau.repository.search.ParcelleSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.ParcelleDTO;
import com.onea.sidot.gestioneau.service.mapper.ParcelleMapper;
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
 * Integration tests for the {@link ParcelleResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class ParcelleResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/parcelles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/parcelles";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ParcelleRepository parcelleRepository;

    @Autowired
    private ParcelleMapper parcelleMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.ParcelleSearchRepositoryMockConfiguration
     */
    @Autowired
    private ParcelleSearchRepository mockParcelleSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Parcelle parcelle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Parcelle createEntity(EntityManager em) {
        Parcelle parcelle = new Parcelle().libelle(DEFAULT_LIBELLE);
        return parcelle;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Parcelle createUpdatedEntity(EntityManager em) {
        Parcelle parcelle = new Parcelle().libelle(UPDATED_LIBELLE);
        return parcelle;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Parcelle.class).block();
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
        parcelle = createEntity(em);
    }

    @Test
    void createParcelle() throws Exception {
        int databaseSizeBeforeCreate = parcelleRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockParcelleSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Parcelle
        ParcelleDTO parcelleDTO = parcelleMapper.toDto(parcelle);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parcelleDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeCreate + 1);
        Parcelle testParcelle = parcelleList.get(parcelleList.size() - 1);
        assertThat(testParcelle.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the Parcelle in Elasticsearch
        verify(mockParcelleSearchRepository, times(1)).save(testParcelle);
    }

    @Test
    void createParcelleWithExistingId() throws Exception {
        // Create the Parcelle with an existing ID
        parcelle.setId(1L);
        ParcelleDTO parcelleDTO = parcelleMapper.toDto(parcelle);

        int databaseSizeBeforeCreate = parcelleRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parcelleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeCreate);

        // Validate the Parcelle in Elasticsearch
        verify(mockParcelleSearchRepository, times(0)).save(parcelle);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = parcelleRepository.findAll().collectList().block().size();
        // set the field null
        parcelle.setLibelle(null);

        // Create the Parcelle, which fails.
        ParcelleDTO parcelleDTO = parcelleMapper.toDto(parcelle);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parcelleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllParcelles() {
        // Initialize the database
        parcelleRepository.save(parcelle).block();

        // Get all the parcelleList
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
            .value(hasItem(parcelle.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }

    @Test
    void getParcelle() {
        // Initialize the database
        parcelleRepository.save(parcelle).block();

        // Get the parcelle
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, parcelle.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(parcelle.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE));
    }

    @Test
    void getNonExistingParcelle() {
        // Get the parcelle
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewParcelle() throws Exception {
        // Configure the mock search repository
        when(mockParcelleSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        parcelleRepository.save(parcelle).block();

        int databaseSizeBeforeUpdate = parcelleRepository.findAll().collectList().block().size();

        // Update the parcelle
        Parcelle updatedParcelle = parcelleRepository.findById(parcelle.getId()).block();
        updatedParcelle.libelle(UPDATED_LIBELLE);
        ParcelleDTO parcelleDTO = parcelleMapper.toDto(updatedParcelle);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, parcelleDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parcelleDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
        Parcelle testParcelle = parcelleList.get(parcelleList.size() - 1);
        assertThat(testParcelle.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the Parcelle in Elasticsearch
        verify(mockParcelleSearchRepository).save(testParcelle);
    }

    @Test
    void putNonExistingParcelle() throws Exception {
        int databaseSizeBeforeUpdate = parcelleRepository.findAll().collectList().block().size();
        parcelle.setId(count.incrementAndGet());

        // Create the Parcelle
        ParcelleDTO parcelleDTO = parcelleMapper.toDto(parcelle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, parcelleDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parcelleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Parcelle in Elasticsearch
        verify(mockParcelleSearchRepository, times(0)).save(parcelle);
    }

    @Test
    void putWithIdMismatchParcelle() throws Exception {
        int databaseSizeBeforeUpdate = parcelleRepository.findAll().collectList().block().size();
        parcelle.setId(count.incrementAndGet());

        // Create the Parcelle
        ParcelleDTO parcelleDTO = parcelleMapper.toDto(parcelle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parcelleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Parcelle in Elasticsearch
        verify(mockParcelleSearchRepository, times(0)).save(parcelle);
    }

    @Test
    void putWithMissingIdPathParamParcelle() throws Exception {
        int databaseSizeBeforeUpdate = parcelleRepository.findAll().collectList().block().size();
        parcelle.setId(count.incrementAndGet());

        // Create the Parcelle
        ParcelleDTO parcelleDTO = parcelleMapper.toDto(parcelle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(parcelleDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Parcelle in Elasticsearch
        verify(mockParcelleSearchRepository, times(0)).save(parcelle);
    }

    @Test
    void partialUpdateParcelleWithPatch() throws Exception {
        // Initialize the database
        parcelleRepository.save(parcelle).block();

        int databaseSizeBeforeUpdate = parcelleRepository.findAll().collectList().block().size();

        // Update the parcelle using partial update
        Parcelle partialUpdatedParcelle = new Parcelle();
        partialUpdatedParcelle.setId(parcelle.getId());

        partialUpdatedParcelle.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedParcelle.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedParcelle))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
        Parcelle testParcelle = parcelleList.get(parcelleList.size() - 1);
        assertThat(testParcelle.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void fullUpdateParcelleWithPatch() throws Exception {
        // Initialize the database
        parcelleRepository.save(parcelle).block();

        int databaseSizeBeforeUpdate = parcelleRepository.findAll().collectList().block().size();

        // Update the parcelle using partial update
        Parcelle partialUpdatedParcelle = new Parcelle();
        partialUpdatedParcelle.setId(parcelle.getId());

        partialUpdatedParcelle.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedParcelle.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedParcelle))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);
        Parcelle testParcelle = parcelleList.get(parcelleList.size() - 1);
        assertThat(testParcelle.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void patchNonExistingParcelle() throws Exception {
        int databaseSizeBeforeUpdate = parcelleRepository.findAll().collectList().block().size();
        parcelle.setId(count.incrementAndGet());

        // Create the Parcelle
        ParcelleDTO parcelleDTO = parcelleMapper.toDto(parcelle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, parcelleDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(parcelleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Parcelle in Elasticsearch
        verify(mockParcelleSearchRepository, times(0)).save(parcelle);
    }

    @Test
    void patchWithIdMismatchParcelle() throws Exception {
        int databaseSizeBeforeUpdate = parcelleRepository.findAll().collectList().block().size();
        parcelle.setId(count.incrementAndGet());

        // Create the Parcelle
        ParcelleDTO parcelleDTO = parcelleMapper.toDto(parcelle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(parcelleDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Parcelle in Elasticsearch
        verify(mockParcelleSearchRepository, times(0)).save(parcelle);
    }

    @Test
    void patchWithMissingIdPathParamParcelle() throws Exception {
        int databaseSizeBeforeUpdate = parcelleRepository.findAll().collectList().block().size();
        parcelle.setId(count.incrementAndGet());

        // Create the Parcelle
        ParcelleDTO parcelleDTO = parcelleMapper.toDto(parcelle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(parcelleDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Parcelle in the database
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Parcelle in Elasticsearch
        verify(mockParcelleSearchRepository, times(0)).save(parcelle);
    }

    @Test
    void deleteParcelle() {
        // Configure the mock search repository
        when(mockParcelleSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockParcelleSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        parcelleRepository.save(parcelle).block();

        int databaseSizeBeforeDelete = parcelleRepository.findAll().collectList().block().size();

        // Delete the parcelle
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, parcelle.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Parcelle> parcelleList = parcelleRepository.findAll().collectList().block();
        assertThat(parcelleList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Parcelle in Elasticsearch
        verify(mockParcelleSearchRepository, times(1)).deleteById(parcelle.getId());
    }

    @Test
    void searchParcelle() {
        // Configure the mock search repository
        when(mockParcelleSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockParcelleSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        parcelleRepository.save(parcelle).block();
        when(mockParcelleSearchRepository.search("id:" + parcelle.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(parcelle));

        // Search the parcelle
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + parcelle.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(parcelle.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }
}
