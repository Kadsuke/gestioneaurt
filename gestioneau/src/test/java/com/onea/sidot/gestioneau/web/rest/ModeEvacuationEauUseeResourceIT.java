package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.ModeEvacuationEauUsee;
import com.onea.sidot.gestioneau.repository.ModeEvacuationEauUseeRepository;
import com.onea.sidot.gestioneau.repository.search.ModeEvacuationEauUseeSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.ModeEvacuationEauUseeDTO;
import com.onea.sidot.gestioneau.service.mapper.ModeEvacuationEauUseeMapper;
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
 * Integration tests for the {@link ModeEvacuationEauUseeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class ModeEvacuationEauUseeResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mode-evacuation-eau-usees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/mode-evacuation-eau-usees";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ModeEvacuationEauUseeRepository modeEvacuationEauUseeRepository;

    @Autowired
    private ModeEvacuationEauUseeMapper modeEvacuationEauUseeMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.ModeEvacuationEauUseeSearchRepositoryMockConfiguration
     */
    @Autowired
    private ModeEvacuationEauUseeSearchRepository mockModeEvacuationEauUseeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ModeEvacuationEauUsee modeEvacuationEauUsee;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ModeEvacuationEauUsee createEntity(EntityManager em) {
        ModeEvacuationEauUsee modeEvacuationEauUsee = new ModeEvacuationEauUsee().libelle(DEFAULT_LIBELLE);
        return modeEvacuationEauUsee;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ModeEvacuationEauUsee createUpdatedEntity(EntityManager em) {
        ModeEvacuationEauUsee modeEvacuationEauUsee = new ModeEvacuationEauUsee().libelle(UPDATED_LIBELLE);
        return modeEvacuationEauUsee;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ModeEvacuationEauUsee.class).block();
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
        modeEvacuationEauUsee = createEntity(em);
    }

    @Test
    void createModeEvacuationEauUsee() throws Exception {
        int databaseSizeBeforeCreate = modeEvacuationEauUseeRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockModeEvacuationEauUseeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the ModeEvacuationEauUsee
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUsee);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll().collectList().block();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeCreate + 1);
        ModeEvacuationEauUsee testModeEvacuationEauUsee = modeEvacuationEauUseeList.get(modeEvacuationEauUseeList.size() - 1);
        assertThat(testModeEvacuationEauUsee.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository, times(1)).save(testModeEvacuationEauUsee);
    }

    @Test
    void createModeEvacuationEauUseeWithExistingId() throws Exception {
        // Create the ModeEvacuationEauUsee with an existing ID
        modeEvacuationEauUsee.setId(1L);
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUsee);

        int databaseSizeBeforeCreate = modeEvacuationEauUseeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll().collectList().block();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeCreate);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository, times(0)).save(modeEvacuationEauUsee);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = modeEvacuationEauUseeRepository.findAll().collectList().block().size();
        // set the field null
        modeEvacuationEauUsee.setLibelle(null);

        // Create the ModeEvacuationEauUsee, which fails.
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUsee);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll().collectList().block();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllModeEvacuationEauUsees() {
        // Initialize the database
        modeEvacuationEauUseeRepository.save(modeEvacuationEauUsee).block();

        // Get all the modeEvacuationEauUseeList
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
            .value(hasItem(modeEvacuationEauUsee.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }

    @Test
    void getModeEvacuationEauUsee() {
        // Initialize the database
        modeEvacuationEauUseeRepository.save(modeEvacuationEauUsee).block();

        // Get the modeEvacuationEauUsee
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, modeEvacuationEauUsee.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(modeEvacuationEauUsee.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE));
    }

    @Test
    void getNonExistingModeEvacuationEauUsee() {
        // Get the modeEvacuationEauUsee
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewModeEvacuationEauUsee() throws Exception {
        // Configure the mock search repository
        when(mockModeEvacuationEauUseeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        modeEvacuationEauUseeRepository.save(modeEvacuationEauUsee).block();

        int databaseSizeBeforeUpdate = modeEvacuationEauUseeRepository.findAll().collectList().block().size();

        // Update the modeEvacuationEauUsee
        ModeEvacuationEauUsee updatedModeEvacuationEauUsee = modeEvacuationEauUseeRepository
            .findById(modeEvacuationEauUsee.getId())
            .block();
        updatedModeEvacuationEauUsee.libelle(UPDATED_LIBELLE);
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(updatedModeEvacuationEauUsee);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, modeEvacuationEauUseeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll().collectList().block();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeUpdate);
        ModeEvacuationEauUsee testModeEvacuationEauUsee = modeEvacuationEauUseeList.get(modeEvacuationEauUseeList.size() - 1);
        assertThat(testModeEvacuationEauUsee.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository).save(testModeEvacuationEauUsee);
    }

    @Test
    void putNonExistingModeEvacuationEauUsee() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacuationEauUseeRepository.findAll().collectList().block().size();
        modeEvacuationEauUsee.setId(count.incrementAndGet());

        // Create the ModeEvacuationEauUsee
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUsee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, modeEvacuationEauUseeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll().collectList().block();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository, times(0)).save(modeEvacuationEauUsee);
    }

    @Test
    void putWithIdMismatchModeEvacuationEauUsee() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacuationEauUseeRepository.findAll().collectList().block().size();
        modeEvacuationEauUsee.setId(count.incrementAndGet());

        // Create the ModeEvacuationEauUsee
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUsee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll().collectList().block();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository, times(0)).save(modeEvacuationEauUsee);
    }

    @Test
    void putWithMissingIdPathParamModeEvacuationEauUsee() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacuationEauUseeRepository.findAll().collectList().block().size();
        modeEvacuationEauUsee.setId(count.incrementAndGet());

        // Create the ModeEvacuationEauUsee
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUsee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll().collectList().block();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository, times(0)).save(modeEvacuationEauUsee);
    }

    @Test
    void partialUpdateModeEvacuationEauUseeWithPatch() throws Exception {
        // Initialize the database
        modeEvacuationEauUseeRepository.save(modeEvacuationEauUsee).block();

        int databaseSizeBeforeUpdate = modeEvacuationEauUseeRepository.findAll().collectList().block().size();

        // Update the modeEvacuationEauUsee using partial update
        ModeEvacuationEauUsee partialUpdatedModeEvacuationEauUsee = new ModeEvacuationEauUsee();
        partialUpdatedModeEvacuationEauUsee.setId(modeEvacuationEauUsee.getId());

        partialUpdatedModeEvacuationEauUsee.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedModeEvacuationEauUsee.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedModeEvacuationEauUsee))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll().collectList().block();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeUpdate);
        ModeEvacuationEauUsee testModeEvacuationEauUsee = modeEvacuationEauUseeList.get(modeEvacuationEauUseeList.size() - 1);
        assertThat(testModeEvacuationEauUsee.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void fullUpdateModeEvacuationEauUseeWithPatch() throws Exception {
        // Initialize the database
        modeEvacuationEauUseeRepository.save(modeEvacuationEauUsee).block();

        int databaseSizeBeforeUpdate = modeEvacuationEauUseeRepository.findAll().collectList().block().size();

        // Update the modeEvacuationEauUsee using partial update
        ModeEvacuationEauUsee partialUpdatedModeEvacuationEauUsee = new ModeEvacuationEauUsee();
        partialUpdatedModeEvacuationEauUsee.setId(modeEvacuationEauUsee.getId());

        partialUpdatedModeEvacuationEauUsee.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedModeEvacuationEauUsee.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedModeEvacuationEauUsee))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll().collectList().block();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeUpdate);
        ModeEvacuationEauUsee testModeEvacuationEauUsee = modeEvacuationEauUseeList.get(modeEvacuationEauUseeList.size() - 1);
        assertThat(testModeEvacuationEauUsee.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void patchNonExistingModeEvacuationEauUsee() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacuationEauUseeRepository.findAll().collectList().block().size();
        modeEvacuationEauUsee.setId(count.incrementAndGet());

        // Create the ModeEvacuationEauUsee
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUsee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, modeEvacuationEauUseeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll().collectList().block();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository, times(0)).save(modeEvacuationEauUsee);
    }

    @Test
    void patchWithIdMismatchModeEvacuationEauUsee() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacuationEauUseeRepository.findAll().collectList().block().size();
        modeEvacuationEauUsee.setId(count.incrementAndGet());

        // Create the ModeEvacuationEauUsee
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUsee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll().collectList().block();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository, times(0)).save(modeEvacuationEauUsee);
    }

    @Test
    void patchWithMissingIdPathParamModeEvacuationEauUsee() throws Exception {
        int databaseSizeBeforeUpdate = modeEvacuationEauUseeRepository.findAll().collectList().block().size();
        modeEvacuationEauUsee.setId(count.incrementAndGet());

        // Create the ModeEvacuationEauUsee
        ModeEvacuationEauUseeDTO modeEvacuationEauUseeDTO = modeEvacuationEauUseeMapper.toDto(modeEvacuationEauUsee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(modeEvacuationEauUseeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ModeEvacuationEauUsee in the database
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll().collectList().block();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository, times(0)).save(modeEvacuationEauUsee);
    }

    @Test
    void deleteModeEvacuationEauUsee() {
        // Configure the mock search repository
        when(mockModeEvacuationEauUseeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockModeEvacuationEauUseeSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        modeEvacuationEauUseeRepository.save(modeEvacuationEauUsee).block();

        int databaseSizeBeforeDelete = modeEvacuationEauUseeRepository.findAll().collectList().block().size();

        // Delete the modeEvacuationEauUsee
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, modeEvacuationEauUsee.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ModeEvacuationEauUsee> modeEvacuationEauUseeList = modeEvacuationEauUseeRepository.findAll().collectList().block();
        assertThat(modeEvacuationEauUseeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ModeEvacuationEauUsee in Elasticsearch
        verify(mockModeEvacuationEauUseeSearchRepository, times(1)).deleteById(modeEvacuationEauUsee.getId());
    }

    @Test
    void searchModeEvacuationEauUsee() {
        // Configure the mock search repository
        when(mockModeEvacuationEauUseeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockModeEvacuationEauUseeSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        modeEvacuationEauUseeRepository.save(modeEvacuationEauUsee).block();
        when(mockModeEvacuationEauUseeSearchRepository.search("id:" + modeEvacuationEauUsee.getId(), PageRequest.of(0, 20)))
            .thenReturn(Flux.just(modeEvacuationEauUsee));

        // Search the modeEvacuationEauUsee
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + modeEvacuationEauUsee.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(modeEvacuationEauUsee.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }
}
