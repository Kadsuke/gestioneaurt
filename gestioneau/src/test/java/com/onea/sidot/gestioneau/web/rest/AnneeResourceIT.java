package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.Annee;
import com.onea.sidot.gestioneau.repository.AnneeRepository;
import com.onea.sidot.gestioneau.repository.search.AnneeSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.AnneeDTO;
import com.onea.sidot.gestioneau.service.mapper.AnneeMapper;
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
 * Integration tests for the {@link AnneeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class AnneeResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/annees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/annees";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AnneeRepository anneeRepository;

    @Autowired
    private AnneeMapper anneeMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.AnneeSearchRepositoryMockConfiguration
     */
    @Autowired
    private AnneeSearchRepository mockAnneeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Annee annee;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Annee createEntity(EntityManager em) {
        Annee annee = new Annee().libelle(DEFAULT_LIBELLE);
        return annee;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Annee createUpdatedEntity(EntityManager em) {
        Annee annee = new Annee().libelle(UPDATED_LIBELLE);
        return annee;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Annee.class).block();
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
        annee = createEntity(em);
    }

    @Test
    void createAnnee() throws Exception {
        int databaseSizeBeforeCreate = anneeRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockAnneeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Annee
        AnneeDTO anneeDTO = anneeMapper.toDto(annee);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(anneeDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll().collectList().block();
        assertThat(anneeList).hasSize(databaseSizeBeforeCreate + 1);
        Annee testAnnee = anneeList.get(anneeList.size() - 1);
        assertThat(testAnnee.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository, times(1)).save(testAnnee);
    }

    @Test
    void createAnneeWithExistingId() throws Exception {
        // Create the Annee with an existing ID
        annee.setId(1L);
        AnneeDTO anneeDTO = anneeMapper.toDto(annee);

        int databaseSizeBeforeCreate = anneeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(anneeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll().collectList().block();
        assertThat(anneeList).hasSize(databaseSizeBeforeCreate);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository, times(0)).save(annee);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = anneeRepository.findAll().collectList().block().size();
        // set the field null
        annee.setLibelle(null);

        // Create the Annee, which fails.
        AnneeDTO anneeDTO = anneeMapper.toDto(annee);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(anneeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Annee> anneeList = anneeRepository.findAll().collectList().block();
        assertThat(anneeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllAnnees() {
        // Initialize the database
        anneeRepository.save(annee).block();

        // Get all the anneeList
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
            .value(hasItem(annee.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }

    @Test
    void getAnnee() {
        // Initialize the database
        anneeRepository.save(annee).block();

        // Get the annee
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, annee.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(annee.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE));
    }

    @Test
    void getNonExistingAnnee() {
        // Get the annee
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewAnnee() throws Exception {
        // Configure the mock search repository
        when(mockAnneeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        anneeRepository.save(annee).block();

        int databaseSizeBeforeUpdate = anneeRepository.findAll().collectList().block().size();

        // Update the annee
        Annee updatedAnnee = anneeRepository.findById(annee.getId()).block();
        updatedAnnee.libelle(UPDATED_LIBELLE);
        AnneeDTO anneeDTO = anneeMapper.toDto(updatedAnnee);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, anneeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(anneeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll().collectList().block();
        assertThat(anneeList).hasSize(databaseSizeBeforeUpdate);
        Annee testAnnee = anneeList.get(anneeList.size() - 1);
        assertThat(testAnnee.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository).save(testAnnee);
    }

    @Test
    void putNonExistingAnnee() throws Exception {
        int databaseSizeBeforeUpdate = anneeRepository.findAll().collectList().block().size();
        annee.setId(count.incrementAndGet());

        // Create the Annee
        AnneeDTO anneeDTO = anneeMapper.toDto(annee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, anneeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(anneeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll().collectList().block();
        assertThat(anneeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository, times(0)).save(annee);
    }

    @Test
    void putWithIdMismatchAnnee() throws Exception {
        int databaseSizeBeforeUpdate = anneeRepository.findAll().collectList().block().size();
        annee.setId(count.incrementAndGet());

        // Create the Annee
        AnneeDTO anneeDTO = anneeMapper.toDto(annee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(anneeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll().collectList().block();
        assertThat(anneeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository, times(0)).save(annee);
    }

    @Test
    void putWithMissingIdPathParamAnnee() throws Exception {
        int databaseSizeBeforeUpdate = anneeRepository.findAll().collectList().block().size();
        annee.setId(count.incrementAndGet());

        // Create the Annee
        AnneeDTO anneeDTO = anneeMapper.toDto(annee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(anneeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll().collectList().block();
        assertThat(anneeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository, times(0)).save(annee);
    }

    @Test
    void partialUpdateAnneeWithPatch() throws Exception {
        // Initialize the database
        anneeRepository.save(annee).block();

        int databaseSizeBeforeUpdate = anneeRepository.findAll().collectList().block().size();

        // Update the annee using partial update
        Annee partialUpdatedAnnee = new Annee();
        partialUpdatedAnnee.setId(annee.getId());

        partialUpdatedAnnee.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAnnee.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAnnee))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll().collectList().block();
        assertThat(anneeList).hasSize(databaseSizeBeforeUpdate);
        Annee testAnnee = anneeList.get(anneeList.size() - 1);
        assertThat(testAnnee.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void fullUpdateAnneeWithPatch() throws Exception {
        // Initialize the database
        anneeRepository.save(annee).block();

        int databaseSizeBeforeUpdate = anneeRepository.findAll().collectList().block().size();

        // Update the annee using partial update
        Annee partialUpdatedAnnee = new Annee();
        partialUpdatedAnnee.setId(annee.getId());

        partialUpdatedAnnee.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAnnee.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAnnee))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll().collectList().block();
        assertThat(anneeList).hasSize(databaseSizeBeforeUpdate);
        Annee testAnnee = anneeList.get(anneeList.size() - 1);
        assertThat(testAnnee.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void patchNonExistingAnnee() throws Exception {
        int databaseSizeBeforeUpdate = anneeRepository.findAll().collectList().block().size();
        annee.setId(count.incrementAndGet());

        // Create the Annee
        AnneeDTO anneeDTO = anneeMapper.toDto(annee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, anneeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(anneeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll().collectList().block();
        assertThat(anneeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository, times(0)).save(annee);
    }

    @Test
    void patchWithIdMismatchAnnee() throws Exception {
        int databaseSizeBeforeUpdate = anneeRepository.findAll().collectList().block().size();
        annee.setId(count.incrementAndGet());

        // Create the Annee
        AnneeDTO anneeDTO = anneeMapper.toDto(annee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(anneeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll().collectList().block();
        assertThat(anneeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository, times(0)).save(annee);
    }

    @Test
    void patchWithMissingIdPathParamAnnee() throws Exception {
        int databaseSizeBeforeUpdate = anneeRepository.findAll().collectList().block().size();
        annee.setId(count.incrementAndGet());

        // Create the Annee
        AnneeDTO anneeDTO = anneeMapper.toDto(annee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(anneeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Annee in the database
        List<Annee> anneeList = anneeRepository.findAll().collectList().block();
        assertThat(anneeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository, times(0)).save(annee);
    }

    @Test
    void deleteAnnee() {
        // Configure the mock search repository
        when(mockAnneeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockAnneeSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        anneeRepository.save(annee).block();

        int databaseSizeBeforeDelete = anneeRepository.findAll().collectList().block().size();

        // Delete the annee
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, annee.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Annee> anneeList = anneeRepository.findAll().collectList().block();
        assertThat(anneeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Annee in Elasticsearch
        verify(mockAnneeSearchRepository, times(1)).deleteById(annee.getId());
    }

    @Test
    void searchAnnee() {
        // Configure the mock search repository
        when(mockAnneeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockAnneeSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        anneeRepository.save(annee).block();
        when(mockAnneeSearchRepository.search("id:" + annee.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(annee));

        // Search the annee
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + annee.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(annee.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }
}
