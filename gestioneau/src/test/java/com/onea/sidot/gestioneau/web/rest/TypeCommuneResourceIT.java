package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.TypeCommune;
import com.onea.sidot.gestioneau.repository.TypeCommuneRepository;
import com.onea.sidot.gestioneau.repository.search.TypeCommuneSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.TypeCommuneDTO;
import com.onea.sidot.gestioneau.service.mapper.TypeCommuneMapper;
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
 * Integration tests for the {@link TypeCommuneResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class TypeCommuneResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/type-communes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/type-communes";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TypeCommuneRepository typeCommuneRepository;

    @Autowired
    private TypeCommuneMapper typeCommuneMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.TypeCommuneSearchRepositoryMockConfiguration
     */
    @Autowired
    private TypeCommuneSearchRepository mockTypeCommuneSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TypeCommune typeCommune;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeCommune createEntity(EntityManager em) {
        TypeCommune typeCommune = new TypeCommune().libelle(DEFAULT_LIBELLE);
        return typeCommune;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeCommune createUpdatedEntity(EntityManager em) {
        TypeCommune typeCommune = new TypeCommune().libelle(UPDATED_LIBELLE);
        return typeCommune;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TypeCommune.class).block();
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
        typeCommune = createEntity(em);
    }

    @Test
    void createTypeCommune() throws Exception {
        int databaseSizeBeforeCreate = typeCommuneRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockTypeCommuneSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the TypeCommune
        TypeCommuneDTO typeCommuneDTO = typeCommuneMapper.toDto(typeCommune);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeCommuneDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the TypeCommune in the database
        List<TypeCommune> typeCommuneList = typeCommuneRepository.findAll().collectList().block();
        assertThat(typeCommuneList).hasSize(databaseSizeBeforeCreate + 1);
        TypeCommune testTypeCommune = typeCommuneList.get(typeCommuneList.size() - 1);
        assertThat(testTypeCommune.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the TypeCommune in Elasticsearch
        verify(mockTypeCommuneSearchRepository, times(1)).save(testTypeCommune);
    }

    @Test
    void createTypeCommuneWithExistingId() throws Exception {
        // Create the TypeCommune with an existing ID
        typeCommune.setId(1L);
        TypeCommuneDTO typeCommuneDTO = typeCommuneMapper.toDto(typeCommune);

        int databaseSizeBeforeCreate = typeCommuneRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeCommuneDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypeCommune in the database
        List<TypeCommune> typeCommuneList = typeCommuneRepository.findAll().collectList().block();
        assertThat(typeCommuneList).hasSize(databaseSizeBeforeCreate);

        // Validate the TypeCommune in Elasticsearch
        verify(mockTypeCommuneSearchRepository, times(0)).save(typeCommune);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = typeCommuneRepository.findAll().collectList().block().size();
        // set the field null
        typeCommune.setLibelle(null);

        // Create the TypeCommune, which fails.
        TypeCommuneDTO typeCommuneDTO = typeCommuneMapper.toDto(typeCommune);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeCommuneDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<TypeCommune> typeCommuneList = typeCommuneRepository.findAll().collectList().block();
        assertThat(typeCommuneList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllTypeCommunes() {
        // Initialize the database
        typeCommuneRepository.save(typeCommune).block();

        // Get all the typeCommuneList
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
            .value(hasItem(typeCommune.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }

    @Test
    void getTypeCommune() {
        // Initialize the database
        typeCommuneRepository.save(typeCommune).block();

        // Get the typeCommune
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, typeCommune.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(typeCommune.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE));
    }

    @Test
    void getNonExistingTypeCommune() {
        // Get the typeCommune
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewTypeCommune() throws Exception {
        // Configure the mock search repository
        when(mockTypeCommuneSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        typeCommuneRepository.save(typeCommune).block();

        int databaseSizeBeforeUpdate = typeCommuneRepository.findAll().collectList().block().size();

        // Update the typeCommune
        TypeCommune updatedTypeCommune = typeCommuneRepository.findById(typeCommune.getId()).block();
        updatedTypeCommune.libelle(UPDATED_LIBELLE);
        TypeCommuneDTO typeCommuneDTO = typeCommuneMapper.toDto(updatedTypeCommune);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, typeCommuneDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeCommuneDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TypeCommune in the database
        List<TypeCommune> typeCommuneList = typeCommuneRepository.findAll().collectList().block();
        assertThat(typeCommuneList).hasSize(databaseSizeBeforeUpdate);
        TypeCommune testTypeCommune = typeCommuneList.get(typeCommuneList.size() - 1);
        assertThat(testTypeCommune.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the TypeCommune in Elasticsearch
        verify(mockTypeCommuneSearchRepository).save(testTypeCommune);
    }

    @Test
    void putNonExistingTypeCommune() throws Exception {
        int databaseSizeBeforeUpdate = typeCommuneRepository.findAll().collectList().block().size();
        typeCommune.setId(count.incrementAndGet());

        // Create the TypeCommune
        TypeCommuneDTO typeCommuneDTO = typeCommuneMapper.toDto(typeCommune);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, typeCommuneDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeCommuneDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypeCommune in the database
        List<TypeCommune> typeCommuneList = typeCommuneRepository.findAll().collectList().block();
        assertThat(typeCommuneList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TypeCommune in Elasticsearch
        verify(mockTypeCommuneSearchRepository, times(0)).save(typeCommune);
    }

    @Test
    void putWithIdMismatchTypeCommune() throws Exception {
        int databaseSizeBeforeUpdate = typeCommuneRepository.findAll().collectList().block().size();
        typeCommune.setId(count.incrementAndGet());

        // Create the TypeCommune
        TypeCommuneDTO typeCommuneDTO = typeCommuneMapper.toDto(typeCommune);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeCommuneDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypeCommune in the database
        List<TypeCommune> typeCommuneList = typeCommuneRepository.findAll().collectList().block();
        assertThat(typeCommuneList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TypeCommune in Elasticsearch
        verify(mockTypeCommuneSearchRepository, times(0)).save(typeCommune);
    }

    @Test
    void putWithMissingIdPathParamTypeCommune() throws Exception {
        int databaseSizeBeforeUpdate = typeCommuneRepository.findAll().collectList().block().size();
        typeCommune.setId(count.incrementAndGet());

        // Create the TypeCommune
        TypeCommuneDTO typeCommuneDTO = typeCommuneMapper.toDto(typeCommune);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeCommuneDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TypeCommune in the database
        List<TypeCommune> typeCommuneList = typeCommuneRepository.findAll().collectList().block();
        assertThat(typeCommuneList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TypeCommune in Elasticsearch
        verify(mockTypeCommuneSearchRepository, times(0)).save(typeCommune);
    }

    @Test
    void partialUpdateTypeCommuneWithPatch() throws Exception {
        // Initialize the database
        typeCommuneRepository.save(typeCommune).block();

        int databaseSizeBeforeUpdate = typeCommuneRepository.findAll().collectList().block().size();

        // Update the typeCommune using partial update
        TypeCommune partialUpdatedTypeCommune = new TypeCommune();
        partialUpdatedTypeCommune.setId(typeCommune.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTypeCommune.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTypeCommune))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TypeCommune in the database
        List<TypeCommune> typeCommuneList = typeCommuneRepository.findAll().collectList().block();
        assertThat(typeCommuneList).hasSize(databaseSizeBeforeUpdate);
        TypeCommune testTypeCommune = typeCommuneList.get(typeCommuneList.size() - 1);
        assertThat(testTypeCommune.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
    }

    @Test
    void fullUpdateTypeCommuneWithPatch() throws Exception {
        // Initialize the database
        typeCommuneRepository.save(typeCommune).block();

        int databaseSizeBeforeUpdate = typeCommuneRepository.findAll().collectList().block().size();

        // Update the typeCommune using partial update
        TypeCommune partialUpdatedTypeCommune = new TypeCommune();
        partialUpdatedTypeCommune.setId(typeCommune.getId());

        partialUpdatedTypeCommune.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTypeCommune.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTypeCommune))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TypeCommune in the database
        List<TypeCommune> typeCommuneList = typeCommuneRepository.findAll().collectList().block();
        assertThat(typeCommuneList).hasSize(databaseSizeBeforeUpdate);
        TypeCommune testTypeCommune = typeCommuneList.get(typeCommuneList.size() - 1);
        assertThat(testTypeCommune.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void patchNonExistingTypeCommune() throws Exception {
        int databaseSizeBeforeUpdate = typeCommuneRepository.findAll().collectList().block().size();
        typeCommune.setId(count.incrementAndGet());

        // Create the TypeCommune
        TypeCommuneDTO typeCommuneDTO = typeCommuneMapper.toDto(typeCommune);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, typeCommuneDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeCommuneDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypeCommune in the database
        List<TypeCommune> typeCommuneList = typeCommuneRepository.findAll().collectList().block();
        assertThat(typeCommuneList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TypeCommune in Elasticsearch
        verify(mockTypeCommuneSearchRepository, times(0)).save(typeCommune);
    }

    @Test
    void patchWithIdMismatchTypeCommune() throws Exception {
        int databaseSizeBeforeUpdate = typeCommuneRepository.findAll().collectList().block().size();
        typeCommune.setId(count.incrementAndGet());

        // Create the TypeCommune
        TypeCommuneDTO typeCommuneDTO = typeCommuneMapper.toDto(typeCommune);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeCommuneDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypeCommune in the database
        List<TypeCommune> typeCommuneList = typeCommuneRepository.findAll().collectList().block();
        assertThat(typeCommuneList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TypeCommune in Elasticsearch
        verify(mockTypeCommuneSearchRepository, times(0)).save(typeCommune);
    }

    @Test
    void patchWithMissingIdPathParamTypeCommune() throws Exception {
        int databaseSizeBeforeUpdate = typeCommuneRepository.findAll().collectList().block().size();
        typeCommune.setId(count.incrementAndGet());

        // Create the TypeCommune
        TypeCommuneDTO typeCommuneDTO = typeCommuneMapper.toDto(typeCommune);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeCommuneDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TypeCommune in the database
        List<TypeCommune> typeCommuneList = typeCommuneRepository.findAll().collectList().block();
        assertThat(typeCommuneList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TypeCommune in Elasticsearch
        verify(mockTypeCommuneSearchRepository, times(0)).save(typeCommune);
    }

    @Test
    void deleteTypeCommune() {
        // Configure the mock search repository
        when(mockTypeCommuneSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockTypeCommuneSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        typeCommuneRepository.save(typeCommune).block();

        int databaseSizeBeforeDelete = typeCommuneRepository.findAll().collectList().block().size();

        // Delete the typeCommune
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, typeCommune.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<TypeCommune> typeCommuneList = typeCommuneRepository.findAll().collectList().block();
        assertThat(typeCommuneList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the TypeCommune in Elasticsearch
        verify(mockTypeCommuneSearchRepository, times(1)).deleteById(typeCommune.getId());
    }

    @Test
    void searchTypeCommune() {
        // Configure the mock search repository
        when(mockTypeCommuneSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockTypeCommuneSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        typeCommuneRepository.save(typeCommune).block();
        when(mockTypeCommuneSearchRepository.search("id:" + typeCommune.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(typeCommune));

        // Search the typeCommune
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + typeCommune.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(typeCommune.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }
}
