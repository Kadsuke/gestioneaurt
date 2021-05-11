package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.TypeHabitation;
import com.onea.sidot.gestioneau.repository.TypeHabitationRepository;
import com.onea.sidot.gestioneau.repository.search.TypeHabitationSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.TypeHabitationDTO;
import com.onea.sidot.gestioneau.service.mapper.TypeHabitationMapper;
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
 * Integration tests for the {@link TypeHabitationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class TypeHabitationResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/type-habitations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/type-habitations";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TypeHabitationRepository typeHabitationRepository;

    @Autowired
    private TypeHabitationMapper typeHabitationMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.TypeHabitationSearchRepositoryMockConfiguration
     */
    @Autowired
    private TypeHabitationSearchRepository mockTypeHabitationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TypeHabitation typeHabitation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeHabitation createEntity(EntityManager em) {
        TypeHabitation typeHabitation = new TypeHabitation().libelle(DEFAULT_LIBELLE);
        return typeHabitation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeHabitation createUpdatedEntity(EntityManager em) {
        TypeHabitation typeHabitation = new TypeHabitation().libelle(UPDATED_LIBELLE);
        return typeHabitation;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TypeHabitation.class).block();
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
        typeHabitation = createEntity(em);
    }

    @Test
    void createTypeHabitation() throws Exception {
        int databaseSizeBeforeCreate = typeHabitationRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockTypeHabitationSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the TypeHabitation
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(typeHabitation);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll().collectList().block();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeCreate + 1);
        TypeHabitation testTypeHabitation = typeHabitationList.get(typeHabitationList.size() - 1);
        assertThat(testTypeHabitation.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository, times(1)).save(testTypeHabitation);
    }

    @Test
    void createTypeHabitationWithExistingId() throws Exception {
        // Create the TypeHabitation with an existing ID
        typeHabitation.setId(1L);
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(typeHabitation);

        int databaseSizeBeforeCreate = typeHabitationRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll().collectList().block();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeCreate);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository, times(0)).save(typeHabitation);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = typeHabitationRepository.findAll().collectList().block().size();
        // set the field null
        typeHabitation.setLibelle(null);

        // Create the TypeHabitation, which fails.
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(typeHabitation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll().collectList().block();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllTypeHabitations() {
        // Initialize the database
        typeHabitationRepository.save(typeHabitation).block();

        // Get all the typeHabitationList
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
            .value(hasItem(typeHabitation.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }

    @Test
    void getTypeHabitation() {
        // Initialize the database
        typeHabitationRepository.save(typeHabitation).block();

        // Get the typeHabitation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, typeHabitation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(typeHabitation.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE));
    }

    @Test
    void getNonExistingTypeHabitation() {
        // Get the typeHabitation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewTypeHabitation() throws Exception {
        // Configure the mock search repository
        when(mockTypeHabitationSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        typeHabitationRepository.save(typeHabitation).block();

        int databaseSizeBeforeUpdate = typeHabitationRepository.findAll().collectList().block().size();

        // Update the typeHabitation
        TypeHabitation updatedTypeHabitation = typeHabitationRepository.findById(typeHabitation.getId()).block();
        updatedTypeHabitation.libelle(UPDATED_LIBELLE);
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(updatedTypeHabitation);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, typeHabitationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll().collectList().block();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeUpdate);
        TypeHabitation testTypeHabitation = typeHabitationList.get(typeHabitationList.size() - 1);
        assertThat(testTypeHabitation.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository).save(testTypeHabitation);
    }

    @Test
    void putNonExistingTypeHabitation() throws Exception {
        int databaseSizeBeforeUpdate = typeHabitationRepository.findAll().collectList().block().size();
        typeHabitation.setId(count.incrementAndGet());

        // Create the TypeHabitation
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(typeHabitation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, typeHabitationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll().collectList().block();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository, times(0)).save(typeHabitation);
    }

    @Test
    void putWithIdMismatchTypeHabitation() throws Exception {
        int databaseSizeBeforeUpdate = typeHabitationRepository.findAll().collectList().block().size();
        typeHabitation.setId(count.incrementAndGet());

        // Create the TypeHabitation
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(typeHabitation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll().collectList().block();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository, times(0)).save(typeHabitation);
    }

    @Test
    void putWithMissingIdPathParamTypeHabitation() throws Exception {
        int databaseSizeBeforeUpdate = typeHabitationRepository.findAll().collectList().block().size();
        typeHabitation.setId(count.incrementAndGet());

        // Create the TypeHabitation
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(typeHabitation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll().collectList().block();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository, times(0)).save(typeHabitation);
    }

    @Test
    void partialUpdateTypeHabitationWithPatch() throws Exception {
        // Initialize the database
        typeHabitationRepository.save(typeHabitation).block();

        int databaseSizeBeforeUpdate = typeHabitationRepository.findAll().collectList().block().size();

        // Update the typeHabitation using partial update
        TypeHabitation partialUpdatedTypeHabitation = new TypeHabitation();
        partialUpdatedTypeHabitation.setId(typeHabitation.getId());

        partialUpdatedTypeHabitation.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTypeHabitation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTypeHabitation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll().collectList().block();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeUpdate);
        TypeHabitation testTypeHabitation = typeHabitationList.get(typeHabitationList.size() - 1);
        assertThat(testTypeHabitation.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void fullUpdateTypeHabitationWithPatch() throws Exception {
        // Initialize the database
        typeHabitationRepository.save(typeHabitation).block();

        int databaseSizeBeforeUpdate = typeHabitationRepository.findAll().collectList().block().size();

        // Update the typeHabitation using partial update
        TypeHabitation partialUpdatedTypeHabitation = new TypeHabitation();
        partialUpdatedTypeHabitation.setId(typeHabitation.getId());

        partialUpdatedTypeHabitation.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTypeHabitation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTypeHabitation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll().collectList().block();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeUpdate);
        TypeHabitation testTypeHabitation = typeHabitationList.get(typeHabitationList.size() - 1);
        assertThat(testTypeHabitation.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void patchNonExistingTypeHabitation() throws Exception {
        int databaseSizeBeforeUpdate = typeHabitationRepository.findAll().collectList().block().size();
        typeHabitation.setId(count.incrementAndGet());

        // Create the TypeHabitation
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(typeHabitation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, typeHabitationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll().collectList().block();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository, times(0)).save(typeHabitation);
    }

    @Test
    void patchWithIdMismatchTypeHabitation() throws Exception {
        int databaseSizeBeforeUpdate = typeHabitationRepository.findAll().collectList().block().size();
        typeHabitation.setId(count.incrementAndGet());

        // Create the TypeHabitation
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(typeHabitation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll().collectList().block();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository, times(0)).save(typeHabitation);
    }

    @Test
    void patchWithMissingIdPathParamTypeHabitation() throws Exception {
        int databaseSizeBeforeUpdate = typeHabitationRepository.findAll().collectList().block().size();
        typeHabitation.setId(count.incrementAndGet());

        // Create the TypeHabitation
        TypeHabitationDTO typeHabitationDTO = typeHabitationMapper.toDto(typeHabitation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(typeHabitationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TypeHabitation in the database
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll().collectList().block();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository, times(0)).save(typeHabitation);
    }

    @Test
    void deleteTypeHabitation() {
        // Configure the mock search repository
        when(mockTypeHabitationSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockTypeHabitationSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        typeHabitationRepository.save(typeHabitation).block();

        int databaseSizeBeforeDelete = typeHabitationRepository.findAll().collectList().block().size();

        // Delete the typeHabitation
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, typeHabitation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<TypeHabitation> typeHabitationList = typeHabitationRepository.findAll().collectList().block();
        assertThat(typeHabitationList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the TypeHabitation in Elasticsearch
        verify(mockTypeHabitationSearchRepository, times(1)).deleteById(typeHabitation.getId());
    }

    @Test
    void searchTypeHabitation() {
        // Configure the mock search repository
        when(mockTypeHabitationSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockTypeHabitationSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        typeHabitationRepository.save(typeHabitation).block();
        when(mockTypeHabitationSearchRepository.search("id:" + typeHabitation.getId(), PageRequest.of(0, 20)))
            .thenReturn(Flux.just(typeHabitation));

        // Search the typeHabitation
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + typeHabitation.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(typeHabitation.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }
}
