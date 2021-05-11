package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.Lot;
import com.onea.sidot.gestioneau.repository.LotRepository;
import com.onea.sidot.gestioneau.repository.search.LotSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.LotDTO;
import com.onea.sidot.gestioneau.service.mapper.LotMapper;
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
 * Integration tests for the {@link LotResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class LotResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/lots";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/lots";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LotRepository lotRepository;

    @Autowired
    private LotMapper lotMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.LotSearchRepositoryMockConfiguration
     */
    @Autowired
    private LotSearchRepository mockLotSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Lot lot;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lot createEntity(EntityManager em) {
        Lot lot = new Lot().libelle(DEFAULT_LIBELLE);
        return lot;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lot createUpdatedEntity(EntityManager em) {
        Lot lot = new Lot().libelle(UPDATED_LIBELLE);
        return lot;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Lot.class).block();
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
        lot = createEntity(em);
    }

    @Test
    void createLot() throws Exception {
        int databaseSizeBeforeCreate = lotRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockLotSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Lot
        LotDTO lotDTO = lotMapper.toDto(lot);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(lotDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Lot in the database
        List<Lot> lotList = lotRepository.findAll().collectList().block();
        assertThat(lotList).hasSize(databaseSizeBeforeCreate + 1);
        Lot testLot = lotList.get(lotList.size() - 1);
        assertThat(testLot.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the Lot in Elasticsearch
        verify(mockLotSearchRepository, times(1)).save(testLot);
    }

    @Test
    void createLotWithExistingId() throws Exception {
        // Create the Lot with an existing ID
        lot.setId(1L);
        LotDTO lotDTO = lotMapper.toDto(lot);

        int databaseSizeBeforeCreate = lotRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(lotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Lot in the database
        List<Lot> lotList = lotRepository.findAll().collectList().block();
        assertThat(lotList).hasSize(databaseSizeBeforeCreate);

        // Validate the Lot in Elasticsearch
        verify(mockLotSearchRepository, times(0)).save(lot);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = lotRepository.findAll().collectList().block().size();
        // set the field null
        lot.setLibelle(null);

        // Create the Lot, which fails.
        LotDTO lotDTO = lotMapper.toDto(lot);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(lotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Lot> lotList = lotRepository.findAll().collectList().block();
        assertThat(lotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllLots() {
        // Initialize the database
        lotRepository.save(lot).block();

        // Get all the lotList
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
            .value(hasItem(lot.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }

    @Test
    void getLot() {
        // Initialize the database
        lotRepository.save(lot).block();

        // Get the lot
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, lot.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(lot.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE));
    }

    @Test
    void getNonExistingLot() {
        // Get the lot
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewLot() throws Exception {
        // Configure the mock search repository
        when(mockLotSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        lotRepository.save(lot).block();

        int databaseSizeBeforeUpdate = lotRepository.findAll().collectList().block().size();

        // Update the lot
        Lot updatedLot = lotRepository.findById(lot.getId()).block();
        updatedLot.libelle(UPDATED_LIBELLE);
        LotDTO lotDTO = lotMapper.toDto(updatedLot);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, lotDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(lotDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Lot in the database
        List<Lot> lotList = lotRepository.findAll().collectList().block();
        assertThat(lotList).hasSize(databaseSizeBeforeUpdate);
        Lot testLot = lotList.get(lotList.size() - 1);
        assertThat(testLot.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the Lot in Elasticsearch
        verify(mockLotSearchRepository).save(testLot);
    }

    @Test
    void putNonExistingLot() throws Exception {
        int databaseSizeBeforeUpdate = lotRepository.findAll().collectList().block().size();
        lot.setId(count.incrementAndGet());

        // Create the Lot
        LotDTO lotDTO = lotMapper.toDto(lot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, lotDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(lotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Lot in the database
        List<Lot> lotList = lotRepository.findAll().collectList().block();
        assertThat(lotList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Lot in Elasticsearch
        verify(mockLotSearchRepository, times(0)).save(lot);
    }

    @Test
    void putWithIdMismatchLot() throws Exception {
        int databaseSizeBeforeUpdate = lotRepository.findAll().collectList().block().size();
        lot.setId(count.incrementAndGet());

        // Create the Lot
        LotDTO lotDTO = lotMapper.toDto(lot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(lotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Lot in the database
        List<Lot> lotList = lotRepository.findAll().collectList().block();
        assertThat(lotList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Lot in Elasticsearch
        verify(mockLotSearchRepository, times(0)).save(lot);
    }

    @Test
    void putWithMissingIdPathParamLot() throws Exception {
        int databaseSizeBeforeUpdate = lotRepository.findAll().collectList().block().size();
        lot.setId(count.incrementAndGet());

        // Create the Lot
        LotDTO lotDTO = lotMapper.toDto(lot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(lotDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Lot in the database
        List<Lot> lotList = lotRepository.findAll().collectList().block();
        assertThat(lotList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Lot in Elasticsearch
        verify(mockLotSearchRepository, times(0)).save(lot);
    }

    @Test
    void partialUpdateLotWithPatch() throws Exception {
        // Initialize the database
        lotRepository.save(lot).block();

        int databaseSizeBeforeUpdate = lotRepository.findAll().collectList().block().size();

        // Update the lot using partial update
        Lot partialUpdatedLot = new Lot();
        partialUpdatedLot.setId(lot.getId());

        partialUpdatedLot.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLot.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedLot))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Lot in the database
        List<Lot> lotList = lotRepository.findAll().collectList().block();
        assertThat(lotList).hasSize(databaseSizeBeforeUpdate);
        Lot testLot = lotList.get(lotList.size() - 1);
        assertThat(testLot.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void fullUpdateLotWithPatch() throws Exception {
        // Initialize the database
        lotRepository.save(lot).block();

        int databaseSizeBeforeUpdate = lotRepository.findAll().collectList().block().size();

        // Update the lot using partial update
        Lot partialUpdatedLot = new Lot();
        partialUpdatedLot.setId(lot.getId());

        partialUpdatedLot.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLot.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedLot))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Lot in the database
        List<Lot> lotList = lotRepository.findAll().collectList().block();
        assertThat(lotList).hasSize(databaseSizeBeforeUpdate);
        Lot testLot = lotList.get(lotList.size() - 1);
        assertThat(testLot.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void patchNonExistingLot() throws Exception {
        int databaseSizeBeforeUpdate = lotRepository.findAll().collectList().block().size();
        lot.setId(count.incrementAndGet());

        // Create the Lot
        LotDTO lotDTO = lotMapper.toDto(lot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, lotDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(lotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Lot in the database
        List<Lot> lotList = lotRepository.findAll().collectList().block();
        assertThat(lotList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Lot in Elasticsearch
        verify(mockLotSearchRepository, times(0)).save(lot);
    }

    @Test
    void patchWithIdMismatchLot() throws Exception {
        int databaseSizeBeforeUpdate = lotRepository.findAll().collectList().block().size();
        lot.setId(count.incrementAndGet());

        // Create the Lot
        LotDTO lotDTO = lotMapper.toDto(lot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(lotDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Lot in the database
        List<Lot> lotList = lotRepository.findAll().collectList().block();
        assertThat(lotList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Lot in Elasticsearch
        verify(mockLotSearchRepository, times(0)).save(lot);
    }

    @Test
    void patchWithMissingIdPathParamLot() throws Exception {
        int databaseSizeBeforeUpdate = lotRepository.findAll().collectList().block().size();
        lot.setId(count.incrementAndGet());

        // Create the Lot
        LotDTO lotDTO = lotMapper.toDto(lot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(lotDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Lot in the database
        List<Lot> lotList = lotRepository.findAll().collectList().block();
        assertThat(lotList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Lot in Elasticsearch
        verify(mockLotSearchRepository, times(0)).save(lot);
    }

    @Test
    void deleteLot() {
        // Configure the mock search repository
        when(mockLotSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockLotSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        lotRepository.save(lot).block();

        int databaseSizeBeforeDelete = lotRepository.findAll().collectList().block().size();

        // Delete the lot
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, lot.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Lot> lotList = lotRepository.findAll().collectList().block();
        assertThat(lotList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Lot in Elasticsearch
        verify(mockLotSearchRepository, times(1)).deleteById(lot.getId());
    }

    @Test
    void searchLot() {
        // Configure the mock search repository
        when(mockLotSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockLotSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        lotRepository.save(lot).block();
        when(mockLotSearchRepository.search("id:" + lot.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(lot));

        // Search the lot
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + lot.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(lot.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }
}
