package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.ModeEvacExcreta;
import com.onea.sidot.gestioneau.repository.ModeEvacExcretaRepository;
import com.onea.sidot.gestioneau.repository.search.ModeEvacExcretaSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.ModeEvacExcretaDTO;
import com.onea.sidot.gestioneau.service.mapper.ModeEvacExcretaMapper;
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
 * Integration tests for the {@link ModeEvacExcretaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class ModeEvacExcretaResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mode-evac-excretas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/mode-evac-excretas";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ModeEvacExcretaRepository modeEvacExcretaRepository;

    @Autowired
    private ModeEvacExcretaMapper modeEvacExcretaMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.ModeEvacExcretaSearchRepositoryMockConfiguration
     */
    @Autowired
    private ModeEvacExcretaSearchRepository mockModeEvacExcretaSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ModeEvacExcreta modeEvacExcreta;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ModeEvacExcreta createEntity(EntityManager em) {
        ModeEvacExcreta modeEvacExcreta = new ModeEvacExcreta().libelle(DEFAULT_LIBELLE);
        return modeEvacExcreta;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ModeEvacExcreta createUpdatedEntity(EntityManager em) {
        ModeEvacExcreta modeEvacExcreta = new ModeEvacExcreta().libelle(UPDATED_LIBELLE);
        return modeEvacExcreta;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ModeEvacExcreta.class).block();
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
        modeEvacExcreta = createEntity(em);
    }

    @Test
    void createModeEvacExcreta() throws Exception {
        int databaseSizeBeforeCreate = modeEvacExcretaRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockModeEvacExcretaSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the ModeEvacExcreta
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(modeEvacExcreta);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll().collectList().block();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeCreate + 1);
        ModeEvacExcreta testModeEvacExcreta = modeEvacExcretaList.get(modeEvacExcretaList.size() - 1);
        assertThat(testModeEvacExcreta.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository, times(1)).save(testModeEvacExcreta);
    }

    @Test
    void createModeEvacExcretaWithExistingId() throws Exception {
        // Create the ModeEvacExcreta with an existing ID
        modeEvacExcreta.setId(1L);
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(modeEvacExcreta);

        int databaseSizeBeforeCreate = modeEvacExcretaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll().collectList().block();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeCreate);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository, times(0)).save(modeEvacExcreta);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = modeEvacExcretaRepository.findAll().collectList().block().size();
        // set the field null
        modeEvacExcreta.setLibelle(null);

        // Create the ModeEvacExcreta, which fails.
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(modeEvacExcreta);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll().collectList().block();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllModeEvacExcretas() {
        // Initialize the database
        modeEvacExcretaRepository.save(modeEvacExcreta).block();

        // Get all the modeEvacExcretaList
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
            .value(hasItem(modeEvacExcreta.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }

    @Test
    void getModeEvacExcreta() {
        // Initialize the database
        modeEvacExcretaRepository.save(modeEvacExcreta).block();

        // Get the modeEvacExcreta
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, modeEvacExcreta.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(modeEvacExcreta.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE));
    }

    @Test
    void getNonExistingModeEvacExcreta() {
        // Get the modeEvacExcreta
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewModeEvacExcreta() throws Exception {
        // Configure the mock search repository
        when(mockModeEvacExcretaSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        modeEvacExcretaRepository.save(modeEvacExcreta).block();

        int databaseSizeBeforeUpdate = modeEvacExcretaRepository.findAll().collectList().block().size();

        // Update the modeEvacExcreta
        ModeEvacExcreta updatedModeEvacExcreta = modeEvacExcretaRepository.findById(modeEvacExcreta.getId()).block();
        updatedModeEvacExcreta.libelle(UPDATED_LIBELLE);
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(updatedModeEvacExcreta);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, modeEvacExcretaDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll().collectList().block();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeUpdate);
        ModeEvacExcreta testModeEvacExcreta = modeEvacExcretaList.get(modeEvacExcretaList.size() - 1);
        assertThat(testModeEvacExcreta.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository).save(testModeEvacExcreta);
    }

    @Test
    void putNonExistingModeEvacExcreta() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacExcretaRepository.findAll().collectList().block().size();
        modeEvacExcreta.setId(count.incrementAndGet());

        // Create the ModeEvacExcreta
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(modeEvacExcreta);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, modeEvacExcretaDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll().collectList().block();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository, times(0)).save(modeEvacExcreta);
    }

    @Test
    void putWithIdMismatchModeEvacExcreta() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacExcretaRepository.findAll().collectList().block().size();
        modeEvacExcreta.setId(count.incrementAndGet());

        // Create the ModeEvacExcreta
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(modeEvacExcreta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll().collectList().block();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository, times(0)).save(modeEvacExcreta);
    }

    @Test
    void putWithMissingIdPathParamModeEvacExcreta() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacExcretaRepository.findAll().collectList().block().size();
        modeEvacExcreta.setId(count.incrementAndGet());

        // Create the ModeEvacExcreta
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(modeEvacExcreta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll().collectList().block();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository, times(0)).save(modeEvacExcreta);
    }

    @Test
    void partialUpdateModeEvacExcretaWithPatch() throws Exception {
        // Initialize the database
        modeEvacExcretaRepository.save(modeEvacExcreta).block();

        int databaseSizeBeforeUpdate = modeEvacExcretaRepository.findAll().collectList().block().size();

        // Update the modeEvacExcreta using partial update
        ModeEvacExcreta partialUpdatedModeEvacExcreta = new ModeEvacExcreta();
        partialUpdatedModeEvacExcreta.setId(modeEvacExcreta.getId());

        partialUpdatedModeEvacExcreta.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedModeEvacExcreta.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedModeEvacExcreta))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll().collectList().block();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeUpdate);
        ModeEvacExcreta testModeEvacExcreta = modeEvacExcretaList.get(modeEvacExcretaList.size() - 1);
        assertThat(testModeEvacExcreta.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void fullUpdateModeEvacExcretaWithPatch() throws Exception {
        // Initialize the database
        modeEvacExcretaRepository.save(modeEvacExcreta).block();

        int databaseSizeBeforeUpdate = modeEvacExcretaRepository.findAll().collectList().block().size();

        // Update the modeEvacExcreta using partial update
        ModeEvacExcreta partialUpdatedModeEvacExcreta = new ModeEvacExcreta();
        partialUpdatedModeEvacExcreta.setId(modeEvacExcreta.getId());

        partialUpdatedModeEvacExcreta.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedModeEvacExcreta.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedModeEvacExcreta))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll().collectList().block();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeUpdate);
        ModeEvacExcreta testModeEvacExcreta = modeEvacExcretaList.get(modeEvacExcretaList.size() - 1);
        assertThat(testModeEvacExcreta.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void patchNonExistingModeEvacExcreta() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacExcretaRepository.findAll().collectList().block().size();
        modeEvacExcreta.setId(count.incrementAndGet());

        // Create the ModeEvacExcreta
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(modeEvacExcreta);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, modeEvacExcretaDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll().collectList().block();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository, times(0)).save(modeEvacExcreta);
    }

    @Test
    void patchWithIdMismatchModeEvacExcreta() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacExcretaRepository.findAll().collectList().block().size();
        modeEvacExcreta.setId(count.incrementAndGet());

        // Create the ModeEvacExcreta
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(modeEvacExcreta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll().collectList().block();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository, times(0)).save(modeEvacExcreta);
    }

    @Test
    void patchWithMissingIdPathParamModeEvacExcreta() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacExcretaRepository.findAll().collectList().block().size();
        modeEvacExcreta.setId(count.incrementAndGet());

        // Create the ModeEvacExcreta
        ModeEvacExcretaDTO modeEvacExcretaDTO = modeEvacExcretaMapper.toDto(modeEvacExcreta);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacExcretaDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ModeEvacExcreta in the database
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll().collectList().block();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository, times(0)).save(modeEvacExcreta);
    }

    @Test
    void deleteModeEvacExcreta() {
        // Configure the mock search repository
        when(mockModeEvacExcretaSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockModeEvacExcretaSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        modeEvacExcretaRepository.save(modeEvacExcreta).block();

        int databaseSizeBeforeDelete = modeEvacExcretaRepository.findAll().collectList().block().size();

        // Delete the modeEvacExcreta
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, modeEvacExcreta.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ModeEvacExcreta> modeEvacExcretaList = modeEvacExcretaRepository.findAll().collectList().block();
        assertThat(modeEvacExcretaList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ModeEvacExcreta in Elasticsearch
        verify(mockModeEvacExcretaSearchRepository, times(1)).deleteById(modeEvacExcreta.getId());
    }

    @Test
    void searchModeEvacExcreta() {
        // Configure the mock search repository
        when(mockModeEvacExcretaSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockModeEvacExcretaSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        modeEvacExcretaRepository.save(modeEvacExcreta).block();
        when(mockModeEvacExcretaSearchRepository.search("id:" + modeEvacExcreta.getId(), PageRequest.of(0, 20)))
            .thenReturn(Flux.just(modeEvacExcreta));

        // Search the modeEvacExcreta
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + modeEvacExcreta.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(modeEvacExcreta.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }
}
