package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.Centre;
import com.onea.sidot.gestioneau.repository.CentreRepository;
import com.onea.sidot.gestioneau.repository.search.CentreSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.CentreDTO;
import com.onea.sidot.gestioneau.service.mapper.CentreMapper;
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
 * Integration tests for the {@link CentreResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class CentreResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String DEFAULT_RESPONSABLE = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSABLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/centres";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/centres";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CentreRepository centreRepository;

    @Autowired
    private CentreMapper centreMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.CentreSearchRepositoryMockConfiguration
     */
    @Autowired
    private CentreSearchRepository mockCentreSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Centre centre;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Centre createEntity(EntityManager em) {
        Centre centre = new Centre().libelle(DEFAULT_LIBELLE).responsable(DEFAULT_RESPONSABLE).contact(DEFAULT_CONTACT);
        return centre;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Centre createUpdatedEntity(EntityManager em) {
        Centre centre = new Centre().libelle(UPDATED_LIBELLE).responsable(UPDATED_RESPONSABLE).contact(UPDATED_CONTACT);
        return centre;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Centre.class).block();
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
        centre = createEntity(em);
    }

    @Test
    void createCentre() throws Exception {
        int databaseSizeBeforeCreate = centreRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockCentreSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Centre
        CentreDTO centreDTO = centreMapper.toDto(centre);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Centre in the database
        List<Centre> centreList = centreRepository.findAll().collectList().block();
        assertThat(centreList).hasSize(databaseSizeBeforeCreate + 1);
        Centre testCentre = centreList.get(centreList.size() - 1);
        assertThat(testCentre.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testCentre.getResponsable()).isEqualTo(DEFAULT_RESPONSABLE);
        assertThat(testCentre.getContact()).isEqualTo(DEFAULT_CONTACT);

        // Validate the Centre in Elasticsearch
        verify(mockCentreSearchRepository, times(1)).save(testCentre);
    }

    @Test
    void createCentreWithExistingId() throws Exception {
        // Create the Centre with an existing ID
        centre.setId(1L);
        CentreDTO centreDTO = centreMapper.toDto(centre);

        int databaseSizeBeforeCreate = centreRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Centre in the database
        List<Centre> centreList = centreRepository.findAll().collectList().block();
        assertThat(centreList).hasSize(databaseSizeBeforeCreate);

        // Validate the Centre in Elasticsearch
        verify(mockCentreSearchRepository, times(0)).save(centre);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = centreRepository.findAll().collectList().block().size();
        // set the field null
        centre.setLibelle(null);

        // Create the Centre, which fails.
        CentreDTO centreDTO = centreMapper.toDto(centre);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Centre> centreList = centreRepository.findAll().collectList().block();
        assertThat(centreList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkResponsableIsRequired() throws Exception {
        int databaseSizeBeforeTest = centreRepository.findAll().collectList().block().size();
        // set the field null
        centre.setResponsable(null);

        // Create the Centre, which fails.
        CentreDTO centreDTO = centreMapper.toDto(centre);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Centre> centreList = centreRepository.findAll().collectList().block();
        assertThat(centreList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkContactIsRequired() throws Exception {
        int databaseSizeBeforeTest = centreRepository.findAll().collectList().block().size();
        // set the field null
        centre.setContact(null);

        // Create the Centre, which fails.
        CentreDTO centreDTO = centreMapper.toDto(centre);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Centre> centreList = centreRepository.findAll().collectList().block();
        assertThat(centreList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCentres() {
        // Initialize the database
        centreRepository.save(centre).block();

        // Get all the centreList
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
            .value(hasItem(centre.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE))
            .jsonPath("$.[*].responsable")
            .value(hasItem(DEFAULT_RESPONSABLE))
            .jsonPath("$.[*].contact")
            .value(hasItem(DEFAULT_CONTACT));
    }

    @Test
    void getCentre() {
        // Initialize the database
        centreRepository.save(centre).block();

        // Get the centre
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, centre.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(centre.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE))
            .jsonPath("$.responsable")
            .value(is(DEFAULT_RESPONSABLE))
            .jsonPath("$.contact")
            .value(is(DEFAULT_CONTACT));
    }

    @Test
    void getNonExistingCentre() {
        // Get the centre
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCentre() throws Exception {
        // Configure the mock search repository
        when(mockCentreSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        centreRepository.save(centre).block();

        int databaseSizeBeforeUpdate = centreRepository.findAll().collectList().block().size();

        // Update the centre
        Centre updatedCentre = centreRepository.findById(centre.getId()).block();
        updatedCentre.libelle(UPDATED_LIBELLE).responsable(UPDATED_RESPONSABLE).contact(UPDATED_CONTACT);
        CentreDTO centreDTO = centreMapper.toDto(updatedCentre);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, centreDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Centre in the database
        List<Centre> centreList = centreRepository.findAll().collectList().block();
        assertThat(centreList).hasSize(databaseSizeBeforeUpdate);
        Centre testCentre = centreList.get(centreList.size() - 1);
        assertThat(testCentre.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testCentre.getResponsable()).isEqualTo(UPDATED_RESPONSABLE);
        assertThat(testCentre.getContact()).isEqualTo(UPDATED_CONTACT);

        // Validate the Centre in Elasticsearch
        verify(mockCentreSearchRepository).save(testCentre);
    }

    @Test
    void putNonExistingCentre() throws Exception {
        int databaseSizeBeforeUpdate = centreRepository.findAll().collectList().block().size();
        centre.setId(count.incrementAndGet());

        // Create the Centre
        CentreDTO centreDTO = centreMapper.toDto(centre);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, centreDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Centre in the database
        List<Centre> centreList = centreRepository.findAll().collectList().block();
        assertThat(centreList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Centre in Elasticsearch
        verify(mockCentreSearchRepository, times(0)).save(centre);
    }

    @Test
    void putWithIdMismatchCentre() throws Exception {
        int databaseSizeBeforeUpdate = centreRepository.findAll().collectList().block().size();
        centre.setId(count.incrementAndGet());

        // Create the Centre
        CentreDTO centreDTO = centreMapper.toDto(centre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Centre in the database
        List<Centre> centreList = centreRepository.findAll().collectList().block();
        assertThat(centreList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Centre in Elasticsearch
        verify(mockCentreSearchRepository, times(0)).save(centre);
    }

    @Test
    void putWithMissingIdPathParamCentre() throws Exception {
        int databaseSizeBeforeUpdate = centreRepository.findAll().collectList().block().size();
        centre.setId(count.incrementAndGet());

        // Create the Centre
        CentreDTO centreDTO = centreMapper.toDto(centre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Centre in the database
        List<Centre> centreList = centreRepository.findAll().collectList().block();
        assertThat(centreList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Centre in Elasticsearch
        verify(mockCentreSearchRepository, times(0)).save(centre);
    }

    @Test
    void partialUpdateCentreWithPatch() throws Exception {
        // Initialize the database
        centreRepository.save(centre).block();

        int databaseSizeBeforeUpdate = centreRepository.findAll().collectList().block().size();

        // Update the centre using partial update
        Centre partialUpdatedCentre = new Centre();
        partialUpdatedCentre.setId(centre.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCentre.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCentre))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Centre in the database
        List<Centre> centreList = centreRepository.findAll().collectList().block();
        assertThat(centreList).hasSize(databaseSizeBeforeUpdate);
        Centre testCentre = centreList.get(centreList.size() - 1);
        assertThat(testCentre.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testCentre.getResponsable()).isEqualTo(DEFAULT_RESPONSABLE);
        assertThat(testCentre.getContact()).isEqualTo(DEFAULT_CONTACT);
    }

    @Test
    void fullUpdateCentreWithPatch() throws Exception {
        // Initialize the database
        centreRepository.save(centre).block();

        int databaseSizeBeforeUpdate = centreRepository.findAll().collectList().block().size();

        // Update the centre using partial update
        Centre partialUpdatedCentre = new Centre();
        partialUpdatedCentre.setId(centre.getId());

        partialUpdatedCentre.libelle(UPDATED_LIBELLE).responsable(UPDATED_RESPONSABLE).contact(UPDATED_CONTACT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCentre.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCentre))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Centre in the database
        List<Centre> centreList = centreRepository.findAll().collectList().block();
        assertThat(centreList).hasSize(databaseSizeBeforeUpdate);
        Centre testCentre = centreList.get(centreList.size() - 1);
        assertThat(testCentre.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testCentre.getResponsable()).isEqualTo(UPDATED_RESPONSABLE);
        assertThat(testCentre.getContact()).isEqualTo(UPDATED_CONTACT);
    }

    @Test
    void patchNonExistingCentre() throws Exception {
        int databaseSizeBeforeUpdate = centreRepository.findAll().collectList().block().size();
        centre.setId(count.incrementAndGet());

        // Create the Centre
        CentreDTO centreDTO = centreMapper.toDto(centre);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, centreDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Centre in the database
        List<Centre> centreList = centreRepository.findAll().collectList().block();
        assertThat(centreList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Centre in Elasticsearch
        verify(mockCentreSearchRepository, times(0)).save(centre);
    }

    @Test
    void patchWithIdMismatchCentre() throws Exception {
        int databaseSizeBeforeUpdate = centreRepository.findAll().collectList().block().size();
        centre.setId(count.incrementAndGet());

        // Create the Centre
        CentreDTO centreDTO = centreMapper.toDto(centre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Centre in the database
        List<Centre> centreList = centreRepository.findAll().collectList().block();
        assertThat(centreList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Centre in Elasticsearch
        verify(mockCentreSearchRepository, times(0)).save(centre);
    }

    @Test
    void patchWithMissingIdPathParamCentre() throws Exception {
        int databaseSizeBeforeUpdate = centreRepository.findAll().collectList().block().size();
        centre.setId(count.incrementAndGet());

        // Create the Centre
        CentreDTO centreDTO = centreMapper.toDto(centre);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(centreDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Centre in the database
        List<Centre> centreList = centreRepository.findAll().collectList().block();
        assertThat(centreList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Centre in Elasticsearch
        verify(mockCentreSearchRepository, times(0)).save(centre);
    }

    @Test
    void deleteCentre() {
        // Configure the mock search repository
        when(mockCentreSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockCentreSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        centreRepository.save(centre).block();

        int databaseSizeBeforeDelete = centreRepository.findAll().collectList().block().size();

        // Delete the centre
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, centre.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Centre> centreList = centreRepository.findAll().collectList().block();
        assertThat(centreList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Centre in Elasticsearch
        verify(mockCentreSearchRepository, times(1)).deleteById(centre.getId());
    }

    @Test
    void searchCentre() {
        // Configure the mock search repository
        when(mockCentreSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockCentreSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        centreRepository.save(centre).block();
        when(mockCentreSearchRepository.search("id:" + centre.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(centre));

        // Search the centre
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + centre.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(centre.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE))
            .jsonPath("$.[*].responsable")
            .value(hasItem(DEFAULT_RESPONSABLE))
            .jsonPath("$.[*].contact")
            .value(hasItem(DEFAULT_CONTACT));
    }
}
