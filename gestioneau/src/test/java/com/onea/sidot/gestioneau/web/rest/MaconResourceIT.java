package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.Macon;
import com.onea.sidot.gestioneau.repository.MaconRepository;
import com.onea.sidot.gestioneau.repository.search.MaconSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.MaconDTO;
import com.onea.sidot.gestioneau.service.mapper.MaconMapper;
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
 * Integration tests for the {@link MaconResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class MaconResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/macons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/macons";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MaconRepository maconRepository;

    @Autowired
    private MaconMapper maconMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.MaconSearchRepositoryMockConfiguration
     */
    @Autowired
    private MaconSearchRepository mockMaconSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Macon macon;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Macon createEntity(EntityManager em) {
        Macon macon = new Macon().libelle(DEFAULT_LIBELLE);
        return macon;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Macon createUpdatedEntity(EntityManager em) {
        Macon macon = new Macon().libelle(UPDATED_LIBELLE);
        return macon;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Macon.class).block();
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
        macon = createEntity(em);
    }

    @Test
    void createMacon() throws Exception {
        int databaseSizeBeforeCreate = maconRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockMaconSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Macon
        MaconDTO maconDTO = maconMapper.toDto(macon);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(maconDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Macon in the database
        List<Macon> maconList = maconRepository.findAll().collectList().block();
        assertThat(maconList).hasSize(databaseSizeBeforeCreate + 1);
        Macon testMacon = maconList.get(maconList.size() - 1);
        assertThat(testMacon.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the Macon in Elasticsearch
        verify(mockMaconSearchRepository, times(1)).save(testMacon);
    }

    @Test
    void createMaconWithExistingId() throws Exception {
        // Create the Macon with an existing ID
        macon.setId(1L);
        MaconDTO maconDTO = maconMapper.toDto(macon);

        int databaseSizeBeforeCreate = maconRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(maconDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Macon in the database
        List<Macon> maconList = maconRepository.findAll().collectList().block();
        assertThat(maconList).hasSize(databaseSizeBeforeCreate);

        // Validate the Macon in Elasticsearch
        verify(mockMaconSearchRepository, times(0)).save(macon);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = maconRepository.findAll().collectList().block().size();
        // set the field null
        macon.setLibelle(null);

        // Create the Macon, which fails.
        MaconDTO maconDTO = maconMapper.toDto(macon);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(maconDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Macon> maconList = maconRepository.findAll().collectList().block();
        assertThat(maconList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllMacons() {
        // Initialize the database
        maconRepository.save(macon).block();

        // Get all the maconList
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
            .value(hasItem(macon.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }

    @Test
    void getMacon() {
        // Initialize the database
        maconRepository.save(macon).block();

        // Get the macon
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, macon.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(macon.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE));
    }

    @Test
    void getNonExistingMacon() {
        // Get the macon
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewMacon() throws Exception {
        // Configure the mock search repository
        when(mockMaconSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        maconRepository.save(macon).block();

        int databaseSizeBeforeUpdate = maconRepository.findAll().collectList().block().size();

        // Update the macon
        Macon updatedMacon = maconRepository.findById(macon.getId()).block();
        updatedMacon.libelle(UPDATED_LIBELLE);
        MaconDTO maconDTO = maconMapper.toDto(updatedMacon);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, maconDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(maconDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Macon in the database
        List<Macon> maconList = maconRepository.findAll().collectList().block();
        assertThat(maconList).hasSize(databaseSizeBeforeUpdate);
        Macon testMacon = maconList.get(maconList.size() - 1);
        assertThat(testMacon.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the Macon in Elasticsearch
        verify(mockMaconSearchRepository).save(testMacon);
    }

    @Test
    void putNonExistingMacon() throws Exception {
        int databaseSizeBeforeUpdate = maconRepository.findAll().collectList().block().size();
        macon.setId(count.incrementAndGet());

        // Create the Macon
        MaconDTO maconDTO = maconMapper.toDto(macon);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, maconDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(maconDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Macon in the database
        List<Macon> maconList = maconRepository.findAll().collectList().block();
        assertThat(maconList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Macon in Elasticsearch
        verify(mockMaconSearchRepository, times(0)).save(macon);
    }

    @Test
    void putWithIdMismatchMacon() throws Exception {
        int databaseSizeBeforeUpdate = maconRepository.findAll().collectList().block().size();
        macon.setId(count.incrementAndGet());

        // Create the Macon
        MaconDTO maconDTO = maconMapper.toDto(macon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(maconDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Macon in the database
        List<Macon> maconList = maconRepository.findAll().collectList().block();
        assertThat(maconList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Macon in Elasticsearch
        verify(mockMaconSearchRepository, times(0)).save(macon);
    }

    @Test
    void putWithMissingIdPathParamMacon() throws Exception {
        int databaseSizeBeforeUpdate = maconRepository.findAll().collectList().block().size();
        macon.setId(count.incrementAndGet());

        // Create the Macon
        MaconDTO maconDTO = maconMapper.toDto(macon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(maconDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Macon in the database
        List<Macon> maconList = maconRepository.findAll().collectList().block();
        assertThat(maconList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Macon in Elasticsearch
        verify(mockMaconSearchRepository, times(0)).save(macon);
    }

    @Test
    void partialUpdateMaconWithPatch() throws Exception {
        // Initialize the database
        maconRepository.save(macon).block();

        int databaseSizeBeforeUpdate = maconRepository.findAll().collectList().block().size();

        // Update the macon using partial update
        Macon partialUpdatedMacon = new Macon();
        partialUpdatedMacon.setId(macon.getId());

        partialUpdatedMacon.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMacon.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedMacon))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Macon in the database
        List<Macon> maconList = maconRepository.findAll().collectList().block();
        assertThat(maconList).hasSize(databaseSizeBeforeUpdate);
        Macon testMacon = maconList.get(maconList.size() - 1);
        assertThat(testMacon.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void fullUpdateMaconWithPatch() throws Exception {
        // Initialize the database
        maconRepository.save(macon).block();

        int databaseSizeBeforeUpdate = maconRepository.findAll().collectList().block().size();

        // Update the macon using partial update
        Macon partialUpdatedMacon = new Macon();
        partialUpdatedMacon.setId(macon.getId());

        partialUpdatedMacon.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMacon.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedMacon))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Macon in the database
        List<Macon> maconList = maconRepository.findAll().collectList().block();
        assertThat(maconList).hasSize(databaseSizeBeforeUpdate);
        Macon testMacon = maconList.get(maconList.size() - 1);
        assertThat(testMacon.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void patchNonExistingMacon() throws Exception {
        int databaseSizeBeforeUpdate = maconRepository.findAll().collectList().block().size();
        macon.setId(count.incrementAndGet());

        // Create the Macon
        MaconDTO maconDTO = maconMapper.toDto(macon);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, maconDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(maconDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Macon in the database
        List<Macon> maconList = maconRepository.findAll().collectList().block();
        assertThat(maconList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Macon in Elasticsearch
        verify(mockMaconSearchRepository, times(0)).save(macon);
    }

    @Test
    void patchWithIdMismatchMacon() throws Exception {
        int databaseSizeBeforeUpdate = maconRepository.findAll().collectList().block().size();
        macon.setId(count.incrementAndGet());

        // Create the Macon
        MaconDTO maconDTO = maconMapper.toDto(macon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(maconDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Macon in the database
        List<Macon> maconList = maconRepository.findAll().collectList().block();
        assertThat(maconList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Macon in Elasticsearch
        verify(mockMaconSearchRepository, times(0)).save(macon);
    }

    @Test
    void patchWithMissingIdPathParamMacon() throws Exception {
        int databaseSizeBeforeUpdate = maconRepository.findAll().collectList().block().size();
        macon.setId(count.incrementAndGet());

        // Create the Macon
        MaconDTO maconDTO = maconMapper.toDto(macon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(maconDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Macon in the database
        List<Macon> maconList = maconRepository.findAll().collectList().block();
        assertThat(maconList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Macon in Elasticsearch
        verify(mockMaconSearchRepository, times(0)).save(macon);
    }

    @Test
    void deleteMacon() {
        // Configure the mock search repository
        when(mockMaconSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockMaconSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        maconRepository.save(macon).block();

        int databaseSizeBeforeDelete = maconRepository.findAll().collectList().block().size();

        // Delete the macon
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, macon.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Macon> maconList = maconRepository.findAll().collectList().block();
        assertThat(maconList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Macon in Elasticsearch
        verify(mockMaconSearchRepository, times(1)).deleteById(macon.getId());
    }

    @Test
    void searchMacon() {
        // Configure the mock search repository
        when(mockMaconSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockMaconSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        maconRepository.save(macon).block();
        when(mockMaconSearchRepository.search("id:" + macon.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(macon));

        // Search the macon
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + macon.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(macon.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }
}
