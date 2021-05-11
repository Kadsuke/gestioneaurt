package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.Region;
import com.onea.sidot.gestioneau.repository.RegionRepository;
import com.onea.sidot.gestioneau.repository.search.RegionSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.RegionDTO;
import com.onea.sidot.gestioneau.service.mapper.RegionMapper;
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
 * Integration tests for the {@link RegionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class RegionResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/regions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/regions";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private RegionMapper regionMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.RegionSearchRepositoryMockConfiguration
     */
    @Autowired
    private RegionSearchRepository mockRegionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Region region;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Region createEntity(EntityManager em) {
        Region region = new Region().libelle(DEFAULT_LIBELLE);
        return region;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Region createUpdatedEntity(EntityManager em) {
        Region region = new Region().libelle(UPDATED_LIBELLE);
        return region;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Region.class).block();
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
        region = createEntity(em);
    }

    @Test
    void createRegion() throws Exception {
        int databaseSizeBeforeCreate = regionRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockRegionSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Region
        RegionDTO regionDTO = regionMapper.toDto(region);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(regionDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeCreate + 1);
        Region testRegion = regionList.get(regionList.size() - 1);
        assertThat(testRegion.getLibelle()).isEqualTo(DEFAULT_LIBELLE);

        // Validate the Region in Elasticsearch
        verify(mockRegionSearchRepository, times(1)).save(testRegion);
    }

    @Test
    void createRegionWithExistingId() throws Exception {
        // Create the Region with an existing ID
        region.setId(1L);
        RegionDTO regionDTO = regionMapper.toDto(region);

        int databaseSizeBeforeCreate = regionRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(regionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeCreate);

        // Validate the Region in Elasticsearch
        verify(mockRegionSearchRepository, times(0)).save(region);
    }

    @Test
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = regionRepository.findAll().collectList().block().size();
        // set the field null
        region.setLibelle(null);

        // Create the Region, which fails.
        RegionDTO regionDTO = regionMapper.toDto(region);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(regionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRegions() {
        // Initialize the database
        regionRepository.save(region).block();

        // Get all the regionList
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
            .value(hasItem(region.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }

    @Test
    void getRegion() {
        // Initialize the database
        regionRepository.save(region).block();

        // Get the region
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, region.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(region.getId().intValue()))
            .jsonPath("$.libelle")
            .value(is(DEFAULT_LIBELLE));
    }

    @Test
    void getNonExistingRegion() {
        // Get the region
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRegion() throws Exception {
        // Configure the mock search repository
        when(mockRegionSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        regionRepository.save(region).block();

        int databaseSizeBeforeUpdate = regionRepository.findAll().collectList().block().size();

        // Update the region
        Region updatedRegion = regionRepository.findById(region.getId()).block();
        updatedRegion.libelle(UPDATED_LIBELLE);
        RegionDTO regionDTO = regionMapper.toDto(updatedRegion);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, regionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(regionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate);
        Region testRegion = regionList.get(regionList.size() - 1);
        assertThat(testRegion.getLibelle()).isEqualTo(UPDATED_LIBELLE);

        // Validate the Region in Elasticsearch
        verify(mockRegionSearchRepository).save(testRegion);
    }

    @Test
    void putNonExistingRegion() throws Exception {
        int databaseSizeBeforeUpdate = regionRepository.findAll().collectList().block().size();
        region.setId(count.incrementAndGet());

        // Create the Region
        RegionDTO regionDTO = regionMapper.toDto(region);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, regionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(regionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Region in Elasticsearch
        verify(mockRegionSearchRepository, times(0)).save(region);
    }

    @Test
    void putWithIdMismatchRegion() throws Exception {
        int databaseSizeBeforeUpdate = regionRepository.findAll().collectList().block().size();
        region.setId(count.incrementAndGet());

        // Create the Region
        RegionDTO regionDTO = regionMapper.toDto(region);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(regionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Region in Elasticsearch
        verify(mockRegionSearchRepository, times(0)).save(region);
    }

    @Test
    void putWithMissingIdPathParamRegion() throws Exception {
        int databaseSizeBeforeUpdate = regionRepository.findAll().collectList().block().size();
        region.setId(count.incrementAndGet());

        // Create the Region
        RegionDTO regionDTO = regionMapper.toDto(region);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(regionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Region in Elasticsearch
        verify(mockRegionSearchRepository, times(0)).save(region);
    }

    @Test
    void partialUpdateRegionWithPatch() throws Exception {
        // Initialize the database
        regionRepository.save(region).block();

        int databaseSizeBeforeUpdate = regionRepository.findAll().collectList().block().size();

        // Update the region using partial update
        Region partialUpdatedRegion = new Region();
        partialUpdatedRegion.setId(region.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRegion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRegion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate);
        Region testRegion = regionList.get(regionList.size() - 1);
        assertThat(testRegion.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
    }

    @Test
    void fullUpdateRegionWithPatch() throws Exception {
        // Initialize the database
        regionRepository.save(region).block();

        int databaseSizeBeforeUpdate = regionRepository.findAll().collectList().block().size();

        // Update the region using partial update
        Region partialUpdatedRegion = new Region();
        partialUpdatedRegion.setId(region.getId());

        partialUpdatedRegion.libelle(UPDATED_LIBELLE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRegion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRegion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate);
        Region testRegion = regionList.get(regionList.size() - 1);
        assertThat(testRegion.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    void patchNonExistingRegion() throws Exception {
        int databaseSizeBeforeUpdate = regionRepository.findAll().collectList().block().size();
        region.setId(count.incrementAndGet());

        // Create the Region
        RegionDTO regionDTO = regionMapper.toDto(region);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, regionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(regionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Region in Elasticsearch
        verify(mockRegionSearchRepository, times(0)).save(region);
    }

    @Test
    void patchWithIdMismatchRegion() throws Exception {
        int databaseSizeBeforeUpdate = regionRepository.findAll().collectList().block().size();
        region.setId(count.incrementAndGet());

        // Create the Region
        RegionDTO regionDTO = regionMapper.toDto(region);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(regionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Region in Elasticsearch
        verify(mockRegionSearchRepository, times(0)).save(region);
    }

    @Test
    void patchWithMissingIdPathParamRegion() throws Exception {
        int databaseSizeBeforeUpdate = regionRepository.findAll().collectList().block().size();
        region.setId(count.incrementAndGet());

        // Create the Region
        RegionDTO regionDTO = regionMapper.toDto(region);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(regionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Region in Elasticsearch
        verify(mockRegionSearchRepository, times(0)).save(region);
    }

    @Test
    void deleteRegion() {
        // Configure the mock search repository
        when(mockRegionSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockRegionSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        regionRepository.save(region).block();

        int databaseSizeBeforeDelete = regionRepository.findAll().collectList().block().size();

        // Delete the region
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, region.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Region in Elasticsearch
        verify(mockRegionSearchRepository, times(1)).deleteById(region.getId());
    }

    @Test
    void searchRegion() {
        // Configure the mock search repository
        when(mockRegionSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockRegionSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        regionRepository.save(region).block();
        when(mockRegionSearchRepository.search("id:" + region.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(region));

        // Search the region
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + region.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(region.getId().intValue()))
            .jsonPath("$.[*].libelle")
            .value(hasItem(DEFAULT_LIBELLE));
    }
}
