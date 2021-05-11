package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.SourceApprovEp;
import com.onea.sidot.gestioneau.repository.SourceApprovEpRepository;
import com.onea.sidot.gestioneau.repository.search.SourceApprovEpSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.SourceApprovEpDTO;
import com.onea.sidot.gestioneau.service.mapper.SourceApprovEpMapper;
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
 * Integration tests for the {@link SourceApprovEpResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class SourceApprovEpResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/source-approv-eps";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/source-approv-eps";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SourceApprovEpRepository sourceApprovEpRepository;

    @Autowired
    private SourceApprovEpMapper sourceApprovEpMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.SourceApprovEpSearchRepositoryMockConfiguration
     */
    @Autowired
    private SourceApprovEpSearchRepository mockSourceApprovEpSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private SourceApprovEp sourceApprovEp;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SourceApprovEp createEntity(EntityManager em) {
        SourceApprovEp sourceApprovEp = new SourceApprovEp().libelle(DEFAULT_LIBELLE);
        return sourceApprovEp;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SourceApprovEp createUpdatedEntity(EntityManager em) {
        SourceApprovEp sourceApprovEp = new SourceApprovEp().libelle(UPDATED_LIBELLE);
        return sourceApprovEp;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(SourceApprovEp.class).block();
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
        sourceApprovEp = createEntity(em);
    }

    @Test
    void createSourceApprovEp() throws Exception {
        int databaseSizeBeforeCreate = sourceApprovEpRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockSourceApprovEpSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the SourceApprovEp
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(sourceApprovEp);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll().collectList().block();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeCreate + 1);
        SourceApprovEp testSourceApprovEp = sourceApprovEpList.get(sourceApprovEpList.size() - 1);
        assertThat(testSourceApprovEp.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository, times(1)).save(testSourceApprovEp);
    }

    @Test
    void createSourceApprovEpWithExistingId() throws Exception {
        // Create the SourceApprovEp with an existing ID
        sourceApprovEp.setId(1L);
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(sourceApprovEp);

        int databaseSizeBeforeCreate = sourceApprovEpRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll().collectList().block();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeCreate);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository, times(0)).save(sourceApprovEp);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = sourceApprovEpRepository.findAll().collectList().block().size();
        // set the field null
        sourceApprovEp.setLibelle(null);

        // Create the SourceApprovEp, which fails.
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(sourceApprovEp);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll().collectList().block();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllSourceApprovEps() {
        // Initialize the database
        sourceApprovEpRepository.save(sourceApprovEp).block();

        // Get all the sourceApprovEpList
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
            .value(hasItem(sourceApprovEp.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }

    @Test
    void getSourceApprovEp() {
        // Initialize the database
        sourceApprovEpRepository.save(sourceApprovEp).block();

        // Get the sourceApprovEp
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, sourceApprovEp.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(sourceApprovEp.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE));
    }

    @Test
    void getNonExistingSourceApprovEp() {
        // Get the sourceApprovEp
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewSourceApprovEp() throws Exception {
        // Configure the mock search repository
        when(mockSourceApprovEpSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        sourceApprovEpRepository.save(sourceApprovEp).block();

        int databaseSizeBeforeUpdate = sourceApprovEpRepository.findAll().collectList().block().size();

        // Update the sourceApprovEp
        SourceApprovEp updatedSourceApprovEp = sourceApprovEpRepository.findById(sourceApprovEp.getId()).block();
        updatedSourceApprovEp.libelle(UPDATED_LIBELLE);
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(updatedSourceApprovEp);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, sourceApprovEpDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll().collectList().block();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeUpdate);
        SourceApprovEp testSourceApprovEp = sourceApprovEpList.get(sourceApprovEpList.size() - 1);
        assertThat(testSourceApprovEp.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository).save(testSourceApprovEp);
    }

    @Test
    void putNonExistingSourceApprovEp() throws Exception {
        int databaseSizeBeforeUpdate = sourceApprovEpRepository.findAll().collectList().block().size();
        sourceApprovEp.setId(count.incrementAndGet());

        // Create the SourceApprovEp
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(sourceApprovEp);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, sourceApprovEpDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll().collectList().block();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository, times(0)).save(sourceApprovEp);
    }

    @Test
    void putWithIdMismatchSourceApprovEp() throws Exception {
        int databaseSizeBeforeUpdate = sourceApprovEpRepository.findAll().collectList().block().size();
        sourceApprovEp.setId(count.incrementAndGet());

        // Create the SourceApprovEp
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(sourceApprovEp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll().collectList().block();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository, times(0)).save(sourceApprovEp);
    }

    @Test
    void putWithMissingIdPathParamSourceApprovEp() throws Exception {
        int databaseSizeBeforeUpdate = sourceApprovEpRepository.findAll().collectList().block().size();
        sourceApprovEp.setId(count.incrementAndGet());

        // Create the SourceApprovEp
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(sourceApprovEp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll().collectList().block();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository, times(0)).save(sourceApprovEp);
    }

    @Test
    void partialUpdateSourceApprovEpWithPatch() throws Exception {
        // Initialize the database
        sourceApprovEpRepository.save(sourceApprovEp).block();

        int databaseSizeBeforeUpdate = sourceApprovEpRepository.findAll().collectList().block().size();

        // Update the sourceApprovEp using partial update
        SourceApprovEp partialUpdatedSourceApprovEp = new SourceApprovEp();
        partialUpdatedSourceApprovEp.setId(sourceApprovEp.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSourceApprovEp.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSourceApprovEp))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll().collectList().block();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeUpdate);
        SourceApprovEp testSourceApprovEp = sourceApprovEpList.get(sourceApprovEpList.size() - 1);
        assertThat(testSourceApprovEp.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
    }

    @Test
    void fullUpdateSourceApprovEpWithPatch() throws Exception {
        // Initialize the database
        sourceApprovEpRepository.save(sourceApprovEp).block();

        int databaseSizeBeforeUpdate = sourceApprovEpRepository.findAll().collectList().block().size();

        // Update the sourceApprovEp using partial update
        SourceApprovEp partialUpdatedSourceApprovEp = new SourceApprovEp();
        partialUpdatedSourceApprovEp.setId(sourceApprovEp.getId());

        partialUpdatedSourceApprovEp.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSourceApprovEp.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSourceApprovEp))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll().collectList().block();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeUpdate);
        SourceApprovEp testSourceApprovEp = sourceApprovEpList.get(sourceApprovEpList.size() - 1);
        assertThat(testSourceApprovEp.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void patchNonExistingSourceApprovEp() throws Exception {
        int databaseSizeBeforeUpdate = sourceApprovEpRepository.findAll().collectList().block().size();
        sourceApprovEp.setId(count.incrementAndGet());

        // Create the SourceApprovEp
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(sourceApprovEp);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, sourceApprovEpDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll().collectList().block();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository, times(0)).save(sourceApprovEp);
    }

    @Test
    void patchWithIdMismatchSourceApprovEp() throws Exception {
        int databaseSizeBeforeUpdate = sourceApprovEpRepository.findAll().collectList().block().size();
        sourceApprovEp.setId(count.incrementAndGet());

        // Create the SourceApprovEp
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(sourceApprovEp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll().collectList().block();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository, times(0)).save(sourceApprovEp);
    }

    @Test
    void patchWithMissingIdPathParamSourceApprovEp() throws Exception {
        int databaseSizeBeforeUpdate = sourceApprovEpRepository.findAll().collectList().block().size();
        sourceApprovEp.setId(count.incrementAndGet());

        // Create the SourceApprovEp
        SourceApprovEpDTO sourceApprovEpDTO = sourceApprovEpMapper.toDto(sourceApprovEp);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(sourceApprovEpDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SourceApprovEp in the database
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll().collectList().block();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeUpdate);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository, times(0)).save(sourceApprovEp);
    }

    @Test
    void deleteSourceApprovEp() {
        // Configure the mock search repository
        when(mockSourceApprovEpSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockSourceApprovEpSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        sourceApprovEpRepository.save(sourceApprovEp).block();

        int databaseSizeBeforeDelete = sourceApprovEpRepository.findAll().collectList().block().size();

        // Delete the sourceApprovEp
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, sourceApprovEp.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<SourceApprovEp> sourceApprovEpList = sourceApprovEpRepository.findAll().collectList().block();
        assertThat(sourceApprovEpList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the SourceApprovEp in Elasticsearch
        verify(mockSourceApprovEpSearchRepository, times(1)).deleteById(sourceApprovEp.getId());
    }

    @Test
    void searchSourceApprovEp() {
        // Configure the mock search repository
        when(mockSourceApprovEpSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockSourceApprovEpSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        sourceApprovEpRepository.save(sourceApprovEp).block();
        when(mockSourceApprovEpSearchRepository.search("id:" + sourceApprovEp.getId(), PageRequest.of(0, 20)))
            .thenReturn(Flux.just(sourceApprovEp));

        // Search the sourceApprovEp
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + sourceApprovEp.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(sourceApprovEp.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }
}
