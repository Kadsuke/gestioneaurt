package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.Section;
import com.onea.sidot.gestioneau.repository.SectionRepository;
import com.onea.sidot.gestioneau.repository.search.SectionSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.SectionDTO;
import com.onea.sidot.gestioneau.service.mapper.SectionMapper;
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
 * Integration tests for the {@link SectionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class SectionResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/sections";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SectionMapper sectionMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.SectionSearchRepositoryMockConfiguration
     */
    @Autowired
    private SectionSearchRepository mockSectionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Section section;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Section createEntity(EntityManager em) {
        Section section = new Section().libelle(DEFAULT_LIBELLE);
        return section;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Section createUpdatedEntity(EntityManager em) {
        Section section = new Section().libelle(UPDATED_LIBELLE);
        return section;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Section.class).block();
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
        section = createEntity(em);
    }

    @Test
    void createSection() throws Exception {
        int databaseSizeBeforeCreate = sectionRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockSectionSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Section
        SectionDTO sectionDTO = sectionMapper.toDto(section);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sectionDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeCreate + 1);
        Section testSection = sectionList.get(sectionList.size() - 1);
        assertThat(testSection.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the Section in Elasticsearch
        verify(mockSectionSearchRepository, times(1)).save(testSection);
    }

    @Test
    void createSectionWithExistingId() throws Exception {
        // Create the Section with an existing ID
        section.setId(1L);
        SectionDTO sectionDTO = sectionMapper.toDto(section);

        int databaseSizeBeforeCreate = sectionRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeCreate);

        // Validate the Section in Elasticsearch
        verify(mockSectionSearchRepository, times(0)).save(section);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = sectionRepository.findAll().collectList().block().size();
        // set the field null
        section.setLibelle(null);

        // Create the Section, which fails.
        SectionDTO sectionDTO = sectionMapper.toDto(section);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllSections() {
        // Initialize the database
        sectionRepository.save(section).block();

        // Get all the sectionList
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
            .value(hasItem(section.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }

    @Test
    void getSection() {
        // Initialize the database
        sectionRepository.save(section).block();

        // Get the section
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, section.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(section.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE));
    }

    @Test
    void getNonExistingSection() {
        // Get the section
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewSection() throws Exception {
        // Configure the mock search repository
        when(mockSectionSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        sectionRepository.save(section).block();

        int databaseSizeBeforeUpdate = sectionRepository.findAll().collectList().block().size();

        // Update the section
        Section updatedSection = sectionRepository.findById(section.getId()).block();
        updatedSection.libelle(UPDATED_LIBELLE);
        SectionDTO sectionDTO = sectionMapper.toDto(updatedSection);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, sectionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sectionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeUpdate);
        Section testSection = sectionList.get(sectionList.size() - 1);
        assertThat(testSection.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the Section in Elasticsearch
        verify(mockSectionSearchRepository).save(testSection);
    }

    @Test
    void putNonExistingSection() throws Exception {
        int databaseSizeBeforeUpdate = sectionRepository.findAll().collectList().block().size();
        section.setId(count.incrementAndGet());

        // Create the Section
        SectionDTO sectionDTO = sectionMapper.toDto(section);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, sectionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Section in Elasticsearch
        verify(mockSectionSearchRepository, times(0)).save(section);
    }

    @Test
    void putWithIdMismatchSection() throws Exception {
        int databaseSizeBeforeUpdate = sectionRepository.findAll().collectList().block().size();
        section.setId(count.incrementAndGet());

        // Create the Section
        SectionDTO sectionDTO = sectionMapper.toDto(section);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Section in Elasticsearch
        verify(mockSectionSearchRepository, times(0)).save(section);
    }

    @Test
    void putWithMissingIdPathParamSection() throws Exception {
        int databaseSizeBeforeUpdate = sectionRepository.findAll().collectList().block().size();
        section.setId(count.incrementAndGet());

        // Create the Section
        SectionDTO sectionDTO = sectionMapper.toDto(section);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sectionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Section in Elasticsearch
        verify(mockSectionSearchRepository, times(0)).save(section);
    }

    @Test
    void partialUpdateSectionWithPatch() throws Exception {
        // Initialize the database
        sectionRepository.save(section).block();

        int databaseSizeBeforeUpdate = sectionRepository.findAll().collectList().block().size();

        // Update the section using partial update
        Section partialUpdatedSection = new Section();
        partialUpdatedSection.setId(section.getId());

        partialUpdatedSection.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSection.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSection))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeUpdate);
        Section testSection = sectionList.get(sectionList.size() - 1);
        assertThat(testSection.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void fullUpdateSectionWithPatch() throws Exception {
        // Initialize the database
        sectionRepository.save(section).block();

        int databaseSizeBeforeUpdate = sectionRepository.findAll().collectList().block().size();

        // Update the section using partial update
        Section partialUpdatedSection = new Section();
        partialUpdatedSection.setId(section.getId());

        partialUpdatedSection.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSection.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSection))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeUpdate);
        Section testSection = sectionList.get(sectionList.size() - 1);
        assertThat(testSection.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void patchNonExistingSection() throws Exception {
        int databaseSizeBeforeUpdate = sectionRepository.findAll().collectList().block().size();
        section.setId(count.incrementAndGet());

        // Create the Section
        SectionDTO sectionDTO = sectionMapper.toDto(section);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, sectionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(sectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Section in Elasticsearch
        verify(mockSectionSearchRepository, times(0)).save(section);
    }

    @Test
    void patchWithIdMismatchSection() throws Exception {
        int databaseSizeBeforeUpdate = sectionRepository.findAll().collectList().block().size();
        section.setId(count.incrementAndGet());

        // Create the Section
        SectionDTO sectionDTO = sectionMapper.toDto(section);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(sectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Section in Elasticsearch
        verify(mockSectionSearchRepository, times(0)).save(section);
    }

    @Test
    void patchWithMissingIdPathParamSection() throws Exception {
        int databaseSizeBeforeUpdate = sectionRepository.findAll().collectList().block().size();
        section.setId(count.incrementAndGet());

        // Create the Section
        SectionDTO sectionDTO = sectionMapper.toDto(section);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(sectionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Section in the database
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Section in Elasticsearch
        verify(mockSectionSearchRepository, times(0)).save(section);
    }

    @Test
    void deleteSection() {
        // Configure the mock search repository
        when(mockSectionSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockSectionSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        sectionRepository.save(section).block();

        int databaseSizeBeforeDelete = sectionRepository.findAll().collectList().block().size();

        // Delete the section
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, section.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Section> sectionList = sectionRepository.findAll().collectList().block();
        assertThat(sectionList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Section in Elasticsearch
        verify(mockSectionSearchRepository, times(1)).deleteById(section.getId());
    }

    @Test
    void searchSection() {
        // Configure the mock search repository
        when(mockSectionSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockSectionSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        sectionRepository.save(section).block();
        when(mockSectionSearchRepository.search("id:" + section.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(section));

        // Search the section
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + section.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(section.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }
}
