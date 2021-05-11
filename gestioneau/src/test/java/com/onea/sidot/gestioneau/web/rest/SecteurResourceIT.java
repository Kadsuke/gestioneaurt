package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.Secteur;
import com.onea.sidot.gestioneau.repository.SecteurRepository;
import com.onea.sidot.gestioneau.repository.search.SecteurSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.SecteurDTO;
import com.onea.sidot.gestioneau.service.mapper.SecteurMapper;
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
 * Integration tests for the {@link SecteurResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class SecteurResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/secteurs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/secteurs";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SecteurRepository secteurRepository;

    @Autowired
    private SecteurMapper secteurMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.SecteurSearchRepositoryMockConfiguration
     */
    @Autowired
    private SecteurSearchRepository mockSecteurSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Secteur secteur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Secteur createEntity(EntityManager em) {
        Secteur secteur = new Secteur().libelle(DEFAULT_LIBELLE);
        return secteur;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Secteur createUpdatedEntity(EntityManager em) {
        Secteur secteur = new Secteur().libelle(UPDATED_LIBELLE);
        return secteur;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Secteur.class).block();
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
        secteur = createEntity(em);
    }

    @Test
    void createSecteur() throws Exception {
        int databaseSizeBeforeCreate = secteurRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockSecteurSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Secteur
        SecteurDTO secteurDTO = secteurMapper.toDto(secteur);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(secteurDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Secteur in the database
        List<Secteur> secteurList = secteurRepository.findAll().collectList().block();
        assertThat(secteurList).hasSize(databaseSizeBeforeCreate + 1);
        Secteur testSecteur = secteurList.get(secteurList.size() - 1);
        assertThat(testSecteur.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the Secteur in Elasticsearch
        verify(mockSecteurSearchRepository, times(1)).save(testSecteur);
    }

    @Test
    void createSecteurWithExistingId() throws Exception {
        // Create the Secteur with an existing ID
        secteur.setId(1L);
        SecteurDTO secteurDTO = secteurMapper.toDto(secteur);

        int databaseSizeBeforeCreate = secteurRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(secteurDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Secteur in the database
        List<Secteur> secteurList = secteurRepository.findAll().collectList().block();
        assertThat(secteurList).hasSize(databaseSizeBeforeCreate);

        // Validate the Secteur in Elasticsearch
        verify(mockSecteurSearchRepository, times(0)).save(secteur);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = secteurRepository.findAll().collectList().block().size();
        // set the field null
        secteur.setLibelle(null);

        // Create the Secteur, which fails.
        SecteurDTO secteurDTO = secteurMapper.toDto(secteur);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(secteurDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Secteur> secteurList = secteurRepository.findAll().collectList().block();
        assertThat(secteurList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllSecteurs() {
        // Initialize the database
        secteurRepository.save(secteur).block();

        // Get all the secteurList
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
            .value(hasItem(secteur.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }

    @Test
    void getSecteur() {
        // Initialize the database
        secteurRepository.save(secteur).block();

        // Get the secteur
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, secteur.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(secteur.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE));
    }

    @Test
    void getNonExistingSecteur() {
        // Get the secteur
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewSecteur() throws Exception {
        // Configure the mock search repository
        when(mockSecteurSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        secteurRepository.save(secteur).block();

        int databaseSizeBeforeUpdate = secteurRepository.findAll().collectList().block().size();

        // Update the secteur
        Secteur updatedSecteur = secteurRepository.findById(secteur.getId()).block();
        updatedSecteur.libelle(UPDATED_LIBELLE);
        SecteurDTO secteurDTO = secteurMapper.toDto(updatedSecteur);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, secteurDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(secteurDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Secteur in the database
        List<Secteur> secteurList = secteurRepository.findAll().collectList().block();
        assertThat(secteurList).hasSize(databaseSizeBeforeUpdate);
        Secteur testSecteur = secteurList.get(secteurList.size() - 1);
        assertThat(testSecteur.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the Secteur in Elasticsearch
        verify(mockSecteurSearchRepository).save(testSecteur);
    }

    @Test
    void putNonExistingSecteur() throws Exception {
        int databaseSizeBeforeUpdate = secteurRepository.findAll().collectList().block().size();
        secteur.setId(count.incrementAndGet());

        // Create the Secteur
        SecteurDTO secteurDTO = secteurMapper.toDto(secteur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, secteurDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(secteurDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Secteur in the database
        List<Secteur> secteurList = secteurRepository.findAll().collectList().block();
        assertThat(secteurList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Secteur in Elasticsearch
        verify(mockSecteurSearchRepository, times(0)).save(secteur);
    }

    @Test
    void putWithIdMismatchSecteur() throws Exception {
        int databaseSizeBeforeUpdate = secteurRepository.findAll().collectList().block().size();
        secteur.setId(count.incrementAndGet());

        // Create the Secteur
        SecteurDTO secteurDTO = secteurMapper.toDto(secteur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(secteurDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Secteur in the database
        List<Secteur> secteurList = secteurRepository.findAll().collectList().block();
        assertThat(secteurList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Secteur in Elasticsearch
        verify(mockSecteurSearchRepository, times(0)).save(secteur);
    }

    @Test
    void putWithMissingIdPathParamSecteur() throws Exception {
        int databaseSizeBeforeUpdate = secteurRepository.findAll().collectList().block().size();
        secteur.setId(count.incrementAndGet());

        // Create the Secteur
        SecteurDTO secteurDTO = secteurMapper.toDto(secteur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(secteurDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Secteur in the database
        List<Secteur> secteurList = secteurRepository.findAll().collectList().block();
        assertThat(secteurList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Secteur in Elasticsearch
        verify(mockSecteurSearchRepository, times(0)).save(secteur);
    }

    @Test
    void partialUpdateSecteurWithPatch() throws Exception {
        // Initialize the database
        secteurRepository.save(secteur).block();

        int databaseSizeBeforeUpdate = secteurRepository.findAll().collectList().block().size();

        // Update the secteur using partial update
        Secteur partialUpdatedSecteur = new Secteur();
        partialUpdatedSecteur.setId(secteur.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSecteur.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSecteur))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Secteur in the database
        List<Secteur> secteurList = secteurRepository.findAll().collectList().block();
        assertThat(secteurList).hasSize(databaseSizeBeforeUpdate);
        Secteur testSecteur = secteurList.get(secteurList.size() - 1);
        assertThat(testSecteur.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
    }

    @Test
    void fullUpdateSecteurWithPatch() throws Exception {
        // Initialize the database
        secteurRepository.save(secteur).block();

        int databaseSizeBeforeUpdate = secteurRepository.findAll().collectList().block().size();

        // Update the secteur using partial update
        Secteur partialUpdatedSecteur = new Secteur();
        partialUpdatedSecteur.setId(secteur.getId());

        partialUpdatedSecteur.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSecteur.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSecteur))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Secteur in the database
        List<Secteur> secteurList = secteurRepository.findAll().collectList().block();
        assertThat(secteurList).hasSize(databaseSizeBeforeUpdate);
        Secteur testSecteur = secteurList.get(secteurList.size() - 1);
        assertThat(testSecteur.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void patchNonExistingSecteur() throws Exception {
        int databaseSizeBeforeUpdate = secteurRepository.findAll().collectList().block().size();
        secteur.setId(count.incrementAndGet());

        // Create the Secteur
        SecteurDTO secteurDTO = secteurMapper.toDto(secteur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, secteurDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(secteurDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Secteur in the database
        List<Secteur> secteurList = secteurRepository.findAll().collectList().block();
        assertThat(secteurList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Secteur in Elasticsearch
        verify(mockSecteurSearchRepository, times(0)).save(secteur);
    }

    @Test
    void patchWithIdMismatchSecteur() throws Exception {
        int databaseSizeBeforeUpdate = secteurRepository.findAll().collectList().block().size();
        secteur.setId(count.incrementAndGet());

        // Create the Secteur
        SecteurDTO secteurDTO = secteurMapper.toDto(secteur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(secteurDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Secteur in the database
        List<Secteur> secteurList = secteurRepository.findAll().collectList().block();
        assertThat(secteurList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Secteur in Elasticsearch
        verify(mockSecteurSearchRepository, times(0)).save(secteur);
    }

    @Test
    void patchWithMissingIdPathParamSecteur() throws Exception {
        int databaseSizeBeforeUpdate = secteurRepository.findAll().collectList().block().size();
        secteur.setId(count.incrementAndGet());

        // Create the Secteur
        SecteurDTO secteurDTO = secteurMapper.toDto(secteur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(secteurDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Secteur in the database
        List<Secteur> secteurList = secteurRepository.findAll().collectList().block();
        assertThat(secteurList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Secteur in Elasticsearch
        verify(mockSecteurSearchRepository, times(0)).save(secteur);
    }

    @Test
    void deleteSecteur() {
        // Configure the mock search repository
        when(mockSecteurSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockSecteurSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        secteurRepository.save(secteur).block();

        int databaseSizeBeforeDelete = secteurRepository.findAll().collectList().block().size();

        // Delete the secteur
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, secteur.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Secteur> secteurList = secteurRepository.findAll().collectList().block();
        assertThat(secteurList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Secteur in Elasticsearch
        verify(mockSecteurSearchRepository, times(1)).deleteById(secteur.getId());
    }

    @Test
    void searchSecteur() {
        // Configure the mock search repository
        when(mockSecteurSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockSecteurSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        secteurRepository.save(secteur).block();
        when(mockSecteurSearchRepository.search("id:" + secteur.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(secteur));

        // Search the secteur
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + secteur.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(secteur.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }
}
