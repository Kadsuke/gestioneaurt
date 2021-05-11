package com.onea.sidot.gestioneau.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.onea.sidot.gestioneau.IntegrationTest;
import com.onea.sidot.gestioneau.domain.FicheSuiviOuvrage;
import com.onea.sidot.gestioneau.repository.FicheSuiviOuvrageRepository;
import com.onea.sidot.gestioneau.repository.search.FicheSuiviOuvrageSearchRepository;
import com.onea.sidot.gestioneau.service.EntityManager;
import com.onea.sidot.gestioneau.service.dto.FicheSuiviOuvrageDTO;
import com.onea.sidot.gestioneau.service.mapper.FicheSuiviOuvrageMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link FicheSuiviOuvrageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class FicheSuiviOuvrageResourceIT {

    private static final String DEFAULT_PRJ_APPUIS = "AAAAAAAAAA";
    private static final String UPDATED_PRJ_APPUIS = "BBBBBBBBBB";

    private static final String DEFAULT_NOM_BENEF = "AAAAAAAAAA";
    private static final String UPDATED_NOM_BENEF = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM_BENEF = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM_BENEF = "BBBBBBBBBB";

    private static final String DEFAULT_PROFESSION_BENEF = "AAAAAAAAAA";
    private static final String UPDATED_PROFESSION_BENEF = "BBBBBBBBBB";

    private static final Long DEFAULT_NB_USAGERS = 1L;
    private static final Long UPDATED_NB_USAGERS = 2L;

    private static final String DEFAULT_CONTACTS = "AAAAAAAAAA";
    private static final String UPDATED_CONTACTS = "BBBBBBBBBB";

    private static final Float DEFAULT_LONGITUDE = 1F;
    private static final Float UPDATED_LONGITUDE = 2F;

    private static final Float DEFAULT_LATITUDE = 1F;
    private static final Float UPDATED_LATITUDE = 2F;

    private static final Instant DEFAULT_DATE_REMISE_DEVIS = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_REMISE_DEVIS = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATE_DEBUT_TRAVAUX = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_DEBUT_TRAVAUX = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATE_FIN_TRAVAUX = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_FIN_TRAVAUX = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_RUE = "AAAAAAAAAA";
    private static final String UPDATED_RUE = "BBBBBBBBBB";

    private static final String DEFAULT_PORTE = "AAAAAAAAAA";
    private static final String UPDATED_PORTE = "BBBBBBBBBB";

    private static final String DEFAULT_COUT_MENAGE = "AAAAAAAAAA";
    private static final String UPDATED_COUT_MENAGE = "BBBBBBBBBB";

    private static final Integer DEFAULT_SUBV_ONEA = 1;
    private static final Integer UPDATED_SUBV_ONEA = 2;

    private static final Integer DEFAULT_SUBV_PROJET = 1;
    private static final Integer UPDATED_SUBV_PROJET = 2;

    private static final Integer DEFAULT_AUTRE_SUBV = 1;
    private static final Integer UPDATED_AUTRE_SUBV = 2;

    private static final Integer DEFAULT_TOLES = 1;
    private static final Integer UPDATED_TOLES = 2;

    private static final String DEFAULT_ANIMATEUR = "AAAAAAAAAA";
    private static final String UPDATED_ANIMATEUR = "BBBBBBBBBB";

    private static final String DEFAULT_SUPERVISEUR = "AAAAAAAAAA";
    private static final String UPDATED_SUPERVISEUR = "BBBBBBBBBB";

    private static final String DEFAULT_CONTROLEUR = "AAAAAAAAAA";
    private static final String UPDATED_CONTROLEUR = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/fiche-suivi-ouvrages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/fiche-suivi-ouvrages";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FicheSuiviOuvrageRepository ficheSuiviOuvrageRepository;

    @Autowired
    private FicheSuiviOuvrageMapper ficheSuiviOuvrageMapper;

    /**
     * This repository is mocked in the com.onea.sidot.gestioneau.repository.search test package.
     *
     * @see com.onea.sidot.gestioneau.repository.search.FicheSuiviOuvrageSearchRepositoryMockConfiguration
     */
    @Autowired
    private FicheSuiviOuvrageSearchRepository mockFicheSuiviOuvrageSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private FicheSuiviOuvrage ficheSuiviOuvrage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FicheSuiviOuvrage createEntity(EntityManager em) {
        FicheSuiviOuvrage ficheSuiviOuvrage = new FicheSuiviOuvrage()
            .prjAppuis(DEFAULT_PRJ_APPUIS)
            .nomBenef(DEFAULT_NOM_BENEF)
            .prenomBenef(DEFAULT_PRENOM_BENEF)
            .professionBenef(DEFAULT_PROFESSION_BENEF)
            .nbUsagers(DEFAULT_NB_USAGERS)
            .contacts(DEFAULT_CONTACTS)
            .longitude(DEFAULT_LONGITUDE)
            .latitude(DEFAULT_LATITUDE)
            .dateRemiseDevis(DEFAULT_DATE_REMISE_DEVIS)
            .dateDebutTravaux(DEFAULT_DATE_DEBUT_TRAVAUX)
            .dateFinTravaux(DEFAULT_DATE_FIN_TRAVAUX)
            .rue(DEFAULT_RUE)
            .porte(DEFAULT_PORTE)
            .coutMenage(DEFAULT_COUT_MENAGE)
            .subvOnea(DEFAULT_SUBV_ONEA)
            .subvProjet(DEFAULT_SUBV_PROJET)
            .autreSubv(DEFAULT_AUTRE_SUBV)
            .toles(DEFAULT_TOLES)
            .animateur(DEFAULT_ANIMATEUR)
            .superviseur(DEFAULT_SUPERVISEUR)
            .controleur(DEFAULT_CONTROLEUR);
        return ficheSuiviOuvrage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FicheSuiviOuvrage createUpdatedEntity(EntityManager em) {
        FicheSuiviOuvrage ficheSuiviOuvrage = new FicheSuiviOuvrage()
            .prjAppuis(UPDATED_PRJ_APPUIS)
            .nomBenef(UPDATED_NOM_BENEF)
            .prenomBenef(UPDATED_PRENOM_BENEF)
            .professionBenef(UPDATED_PROFESSION_BENEF)
            .nbUsagers(UPDATED_NB_USAGERS)
            .contacts(UPDATED_CONTACTS)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .dateRemiseDevis(UPDATED_DATE_REMISE_DEVIS)
            .dateDebutTravaux(UPDATED_DATE_DEBUT_TRAVAUX)
            .dateFinTravaux(UPDATED_DATE_FIN_TRAVAUX)
            .rue(UPDATED_RUE)
            .porte(UPDATED_PORTE)
            .coutMenage(UPDATED_COUT_MENAGE)
            .subvOnea(UPDATED_SUBV_ONEA)
            .subvProjet(UPDATED_SUBV_PROJET)
            .autreSubv(UPDATED_AUTRE_SUBV)
            .toles(UPDATED_TOLES)
            .animateur(UPDATED_ANIMATEUR)
            .superviseur(UPDATED_SUPERVISEUR)
            .controleur(UPDATED_CONTROLEUR);
        return ficheSuiviOuvrage;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(FicheSuiviOuvrage.class).block();
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
        ficheSuiviOuvrage = createEntity(em);
    }

    @Test
    void createFicheSuiviOuvrage() throws Exception {
        int databaseSizeBeforeCreate = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockFicheSuiviOuvrageSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the FicheSuiviOuvrage
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeCreate + 1);
        FicheSuiviOuvrage testFicheSuiviOuvrage = ficheSuiviOuvrageList.get(ficheSuiviOuvrageList.size() - 1);
        assertThat(testFicheSuiviOuvrage.getPrjAppuis()).isEqualTo(DEFAULT_PRJ_APPUIS);
        assertThat(testFicheSuiviOuvrage.getNomBenef()).isEqualTo(DEFAULT_NOM_BENEF);
        assertThat(testFicheSuiviOuvrage.getPrenomBenef()).isEqualTo(DEFAULT_PRENOM_BENEF);
        assertThat(testFicheSuiviOuvrage.getProfessionBenef()).isEqualTo(DEFAULT_PROFESSION_BENEF);
        assertThat(testFicheSuiviOuvrage.getNbUsagers()).isEqualTo(DEFAULT_NB_USAGERS);
        assertThat(testFicheSuiviOuvrage.getContacts()).isEqualTo(DEFAULT_CONTACTS);
        assertThat(testFicheSuiviOuvrage.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testFicheSuiviOuvrage.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testFicheSuiviOuvrage.getDateRemiseDevis()).isEqualTo(DEFAULT_DATE_REMISE_DEVIS);
        assertThat(testFicheSuiviOuvrage.getDateDebutTravaux()).isEqualTo(DEFAULT_DATE_DEBUT_TRAVAUX);
        assertThat(testFicheSuiviOuvrage.getDateFinTravaux()).isEqualTo(DEFAULT_DATE_FIN_TRAVAUX);
        assertThat(testFicheSuiviOuvrage.getRue()).isEqualTo(DEFAULT_RUE);
        assertThat(testFicheSuiviOuvrage.getPorte()).isEqualTo(DEFAULT_PORTE);
        assertThat(testFicheSuiviOuvrage.getCoutMenage()).isEqualTo(DEFAULT_COUT_MENAGE);
        assertThat(testFicheSuiviOuvrage.getSubvOnea()).isEqualTo(DEFAULT_SUBV_ONEA);
        assertThat(testFicheSuiviOuvrage.getSubvProjet()).isEqualTo(DEFAULT_SUBV_PROJET);
        assertThat(testFicheSuiviOuvrage.getAutreSubv()).isEqualTo(DEFAULT_AUTRE_SUBV);
        assertThat(testFicheSuiviOuvrage.getToles()).isEqualTo(DEFAULT_TOLES);
        assertThat(testFicheSuiviOuvrage.getAnimateur()).isEqualTo(DEFAULT_ANIMATEUR);
        assertThat(testFicheSuiviOuvrage.getSuperviseur()).isEqualTo(DEFAULT_SUPERVISEUR);
        assertThat(testFicheSuiviOuvrage.getControleur()).isEqualTo(DEFAULT_CONTROLEUR);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository, times(1)).save(testFicheSuiviOuvrage);
    }

    @Test
    void createFicheSuiviOuvrageWithExistingId() throws Exception {
        // Create the FicheSuiviOuvrage with an existing ID
        ficheSuiviOuvrage.setId(1L);
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        int databaseSizeBeforeCreate = ficheSuiviOuvrageRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeCreate);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository, times(0)).save(ficheSuiviOuvrage);
    }

    @Test
    void checkPrjAppuisIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setPrjAppuis(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkNomBenefIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setNomBenef(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPrenomBenefIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setPrenomBenef(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkProfessionBenefIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setProfessionBenef(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkNbUsagersIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setNbUsagers(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkContactsIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setContacts(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLongitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setLongitude(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLatitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setLatitude(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDateRemiseDevisIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setDateRemiseDevis(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDateDebutTravauxIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setDateDebutTravaux(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDateFinTravauxIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setDateFinTravaux(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCoutMenageIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setCoutMenage(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkSubvOneaIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setSubvOnea(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkSubvProjetIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setSubvProjet(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAutreSubvIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setAutreSubv(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkTolesIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setToles(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAnimateurIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setAnimateur(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkSuperviseurIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setSuperviseur(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkControleurIsRequired() throws Exception {
        int databaseSizeBeforeTest = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        // set the field null
        ficheSuiviOuvrage.setControleur(null);

        // Create the FicheSuiviOuvrage, which fails.
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllFicheSuiviOuvrages() {
        // Initialize the database
        ficheSuiviOuvrageRepository.save(ficheSuiviOuvrage).block();

        // Get all the ficheSuiviOuvrageList
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
            .value(hasItem(ficheSuiviOuvrage.getId().intValue()))
            .jsonPath("$.[*].prjAppuis")
            .value(hasItem(DEFAULT_PRJ_APPUIS))
            .jsonPath("$.[*].nomBenef")
            .value(hasItem(DEFAULT_NOM_BENEF))
            .jsonPath("$.[*].prenomBenef")
            .value(hasItem(DEFAULT_PRENOM_BENEF))
            .jsonPath("$.[*].professionBenef")
            .value(hasItem(DEFAULT_PROFESSION_BENEF))
            .jsonPath("$.[*].nbUsagers")
            .value(hasItem(DEFAULT_NB_USAGERS.intValue()))
            .jsonPath("$.[*].contacts")
            .value(hasItem(DEFAULT_CONTACTS))
            .jsonPath("$.[*].longitude")
            .value(hasItem(DEFAULT_LONGITUDE.doubleValue()))
            .jsonPath("$.[*].latitude")
            .value(hasItem(DEFAULT_LATITUDE.doubleValue()))
            .jsonPath("$.[*].dateRemiseDevis")
            .value(hasItem(DEFAULT_DATE_REMISE_DEVIS.toString()))
            .jsonPath("$.[*].dateDebutTravaux")
            .value(hasItem(DEFAULT_DATE_DEBUT_TRAVAUX.toString()))
            .jsonPath("$.[*].dateFinTravaux")
            .value(hasItem(DEFAULT_DATE_FIN_TRAVAUX.toString()))
            .jsonPath("$.[*].rue")
            .value(hasItem(DEFAULT_RUE))
            .jsonPath("$.[*].porte")
            .value(hasItem(DEFAULT_PORTE))
            .jsonPath("$.[*].coutMenage")
            .value(hasItem(DEFAULT_COUT_MENAGE))
            .jsonPath("$.[*].subvOnea")
            .value(hasItem(DEFAULT_SUBV_ONEA))
            .jsonPath("$.[*].subvProjet")
            .value(hasItem(DEFAULT_SUBV_PROJET))
            .jsonPath("$.[*].autreSubv")
            .value(hasItem(DEFAULT_AUTRE_SUBV))
            .jsonPath("$.[*].toles")
            .value(hasItem(DEFAULT_TOLES))
            .jsonPath("$.[*].animateur")
            .value(hasItem(DEFAULT_ANIMATEUR))
            .jsonPath("$.[*].superviseur")
            .value(hasItem(DEFAULT_SUPERVISEUR))
            .jsonPath("$.[*].controleur")
            .value(hasItem(DEFAULT_CONTROLEUR));
    }

    @Test
    void getFicheSuiviOuvrage() {
        // Initialize the database
        ficheSuiviOuvrageRepository.save(ficheSuiviOuvrage).block();

        // Get the ficheSuiviOuvrage
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, ficheSuiviOuvrage.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(ficheSuiviOuvrage.getId().intValue()))
            .jsonPath("$.prjAppuis")
            .value(is(DEFAULT_PRJ_APPUIS))
            .jsonPath("$.nomBenef")
            .value(is(DEFAULT_NOM_BENEF))
            .jsonPath("$.prenomBenef")
            .value(is(DEFAULT_PRENOM_BENEF))
            .jsonPath("$.professionBenef")
            .value(is(DEFAULT_PROFESSION_BENEF))
            .jsonPath("$.nbUsagers")
            .value(is(DEFAULT_NB_USAGERS.intValue()))
            .jsonPath("$.contacts")
            .value(is(DEFAULT_CONTACTS))
            .jsonPath("$.longitude")
            .value(is(DEFAULT_LONGITUDE.doubleValue()))
            .jsonPath("$.latitude")
            .value(is(DEFAULT_LATITUDE.doubleValue()))
            .jsonPath("$.dateRemiseDevis")
            .value(is(DEFAULT_DATE_REMISE_DEVIS.toString()))
            .jsonPath("$.dateDebutTravaux")
            .value(is(DEFAULT_DATE_DEBUT_TRAVAUX.toString()))
            .jsonPath("$.dateFinTravaux")
            .value(is(DEFAULT_DATE_FIN_TRAVAUX.toString()))
            .jsonPath("$.rue")
            .value(is(DEFAULT_RUE))
            .jsonPath("$.porte")
            .value(is(DEFAULT_PORTE))
            .jsonPath("$.coutMenage")
            .value(is(DEFAULT_COUT_MENAGE))
            .jsonPath("$.subvOnea")
            .value(is(DEFAULT_SUBV_ONEA))
            .jsonPath("$.subvProjet")
            .value(is(DEFAULT_SUBV_PROJET))
            .jsonPath("$.autreSubv")
            .value(is(DEFAULT_AUTRE_SUBV))
            .jsonPath("$.toles")
            .value(is(DEFAULT_TOLES))
            .jsonPath("$.animateur")
            .value(is(DEFAULT_ANIMATEUR))
            .jsonPath("$.superviseur")
            .value(is(DEFAULT_SUPERVISEUR))
            .jsonPath("$.controleur")
            .value(is(DEFAULT_CONTROLEUR));
    }

    @Test
    void getNonExistingFicheSuiviOuvrage() {
        // Get the ficheSuiviOuvrage
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewFicheSuiviOuvrage() throws Exception {
        // Configure the mock search repository
        when(mockFicheSuiviOuvrageSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        ficheSuiviOuvrageRepository.save(ficheSuiviOuvrage).block();

        int databaseSizeBeforeUpdate = ficheSuiviOuvrageRepository.findAll().collectList().block().size();

        // Update the ficheSuiviOuvrage
        FicheSuiviOuvrage updatedFicheSuiviOuvrage = ficheSuiviOuvrageRepository.findById(ficheSuiviOuvrage.getId()).block();
        updatedFicheSuiviOuvrage
            .prjAppuis(UPDATED_PRJ_APPUIS)
            .nomBenef(UPDATED_NOM_BENEF)
            .prenomBenef(UPDATED_PRENOM_BENEF)
            .professionBenef(UPDATED_PROFESSION_BENEF)
            .nbUsagers(UPDATED_NB_USAGERS)
            .contacts(UPDATED_CONTACTS)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .dateRemiseDevis(UPDATED_DATE_REMISE_DEVIS)
            .dateDebutTravaux(UPDATED_DATE_DEBUT_TRAVAUX)
            .dateFinTravaux(UPDATED_DATE_FIN_TRAVAUX)
            .rue(UPDATED_RUE)
            .porte(UPDATED_PORTE)
            .coutMenage(UPDATED_COUT_MENAGE)
            .subvOnea(UPDATED_SUBV_ONEA)
            .subvProjet(UPDATED_SUBV_PROJET)
            .autreSubv(UPDATED_AUTRE_SUBV)
            .toles(UPDATED_TOLES)
            .animateur(UPDATED_ANIMATEUR)
            .superviseur(UPDATED_SUPERVISEUR)
            .controleur(UPDATED_CONTROLEUR);
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(updatedFicheSuiviOuvrage);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ficheSuiviOuvrageDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeUpdate);
        FicheSuiviOuvrage testFicheSuiviOuvrage = ficheSuiviOuvrageList.get(ficheSuiviOuvrageList.size() - 1);
        assertThat(testFicheSuiviOuvrage.getPrjAppuis()).isEqualTo(UPDATED_PRJ_APPUIS);
        assertThat(testFicheSuiviOuvrage.getNomBenef()).isEqualTo(UPDATED_NOM_BENEF);
        assertThat(testFicheSuiviOuvrage.getPrenomBenef()).isEqualTo(UPDATED_PRENOM_BENEF);
        assertThat(testFicheSuiviOuvrage.getProfessionBenef()).isEqualTo(UPDATED_PROFESSION_BENEF);
        assertThat(testFicheSuiviOuvrage.getNbUsagers()).isEqualTo(UPDATED_NB_USAGERS);
        assertThat(testFicheSuiviOuvrage.getContacts()).isEqualTo(UPDATED_CONTACTS);
        assertThat(testFicheSuiviOuvrage.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testFicheSuiviOuvrage.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testFicheSuiviOuvrage.getDateRemiseDevis()).isEqualTo(UPDATED_DATE_REMISE_DEVIS);
        assertThat(testFicheSuiviOuvrage.getDateDebutTravaux()).isEqualTo(UPDATED_DATE_DEBUT_TRAVAUX);
        assertThat(testFicheSuiviOuvrage.getDateFinTravaux()).isEqualTo(UPDATED_DATE_FIN_TRAVAUX);
        assertThat(testFicheSuiviOuvrage.getRue()).isEqualTo(UPDATED_RUE);
        assertThat(testFicheSuiviOuvrage.getPorte()).isEqualTo(UPDATED_PORTE);
        assertThat(testFicheSuiviOuvrage.getCoutMenage()).isEqualTo(UPDATED_COUT_MENAGE);
        assertThat(testFicheSuiviOuvrage.getSubvOnea()).isEqualTo(UPDATED_SUBV_ONEA);
        assertThat(testFicheSuiviOuvrage.getSubvProjet()).isEqualTo(UPDATED_SUBV_PROJET);
        assertThat(testFicheSuiviOuvrage.getAutreSubv()).isEqualTo(UPDATED_AUTRE_SUBV);
        assertThat(testFicheSuiviOuvrage.getToles()).isEqualTo(UPDATED_TOLES);
        assertThat(testFicheSuiviOuvrage.getAnimateur()).isEqualTo(UPDATED_ANIMATEUR);
        assertThat(testFicheSuiviOuvrage.getSuperviseur()).isEqualTo(UPDATED_SUPERVISEUR);
        assertThat(testFicheSuiviOuvrage.getControleur()).isEqualTo(UPDATED_CONTROLEUR);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository).save(testFicheSuiviOuvrage);
    }

    @Test
    void putNonExistingFicheSuiviOuvrage() throws Exception {
        int databaseSizeBeforeUpdate = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        ficheSuiviOuvrage.setId(count.incrementAndGet());

        // Create the FicheSuiviOuvrage
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ficheSuiviOuvrageDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository, times(0)).save(ficheSuiviOuvrage);
    }

    @Test
    void putWithIdMismatchFicheSuiviOuvrage() throws Exception {
        int databaseSizeBeforeUpdate = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        ficheSuiviOuvrage.setId(count.incrementAndGet());

        // Create the FicheSuiviOuvrage
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository, times(0)).save(ficheSuiviOuvrage);
    }

    @Test
    void putWithMissingIdPathParamFicheSuiviOuvrage() throws Exception {
        int databaseSizeBeforeUpdate = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        ficheSuiviOuvrage.setId(count.incrementAndGet());

        // Create the FicheSuiviOuvrage
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository, times(0)).save(ficheSuiviOuvrage);
    }

    @Test
    void partialUpdateFicheSuiviOuvrageWithPatch() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.save(ficheSuiviOuvrage).block();

        int databaseSizeBeforeUpdate = ficheSuiviOuvrageRepository.findAll().collectList().block().size();

        // Update the ficheSuiviOuvrage using partial update
        FicheSuiviOuvrage partialUpdatedFicheSuiviOuvrage = new FicheSuiviOuvrage();
        partialUpdatedFicheSuiviOuvrage.setId(ficheSuiviOuvrage.getId());

        partialUpdatedFicheSuiviOuvrage
            .nbUsagers(UPDATED_NB_USAGERS)
            .contacts(UPDATED_CONTACTS)
            .dateRemiseDevis(UPDATED_DATE_REMISE_DEVIS)
            .dateFinTravaux(UPDATED_DATE_FIN_TRAVAUX)
            .rue(UPDATED_RUE)
            .coutMenage(UPDATED_COUT_MENAGE)
            .subvProjet(UPDATED_SUBV_PROJET)
            .autreSubv(UPDATED_AUTRE_SUBV)
            .toles(UPDATED_TOLES)
            .animateur(UPDATED_ANIMATEUR)
            .superviseur(UPDATED_SUPERVISEUR)
            .controleur(UPDATED_CONTROLEUR);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFicheSuiviOuvrage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFicheSuiviOuvrage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeUpdate);
        FicheSuiviOuvrage testFicheSuiviOuvrage = ficheSuiviOuvrageList.get(ficheSuiviOuvrageList.size() - 1);
        assertThat(testFicheSuiviOuvrage.getPrjAppuis()).isEqualTo(DEFAULT_PRJ_APPUIS);
        assertThat(testFicheSuiviOuvrage.getNomBenef()).isEqualTo(DEFAULT_NOM_BENEF);
        assertThat(testFicheSuiviOuvrage.getPrenomBenef()).isEqualTo(DEFAULT_PRENOM_BENEF);
        assertThat(testFicheSuiviOuvrage.getProfessionBenef()).isEqualTo(DEFAULT_PROFESSION_BENEF);
        assertThat(testFicheSuiviOuvrage.getNbUsagers()).isEqualTo(UPDATED_NB_USAGERS);
        assertThat(testFicheSuiviOuvrage.getContacts()).isEqualTo(UPDATED_CONTACTS);
        assertThat(testFicheSuiviOuvrage.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testFicheSuiviOuvrage.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testFicheSuiviOuvrage.getDateRemiseDevis()).isEqualTo(UPDATED_DATE_REMISE_DEVIS);
        assertThat(testFicheSuiviOuvrage.getDateDebutTravaux()).isEqualTo(DEFAULT_DATE_DEBUT_TRAVAUX);
        assertThat(testFicheSuiviOuvrage.getDateFinTravaux()).isEqualTo(UPDATED_DATE_FIN_TRAVAUX);
        assertThat(testFicheSuiviOuvrage.getRue()).isEqualTo(UPDATED_RUE);
        assertThat(testFicheSuiviOuvrage.getPorte()).isEqualTo(DEFAULT_PORTE);
        assertThat(testFicheSuiviOuvrage.getCoutMenage()).isEqualTo(UPDATED_COUT_MENAGE);
        assertThat(testFicheSuiviOuvrage.getSubvOnea()).isEqualTo(DEFAULT_SUBV_ONEA);
        assertThat(testFicheSuiviOuvrage.getSubvProjet()).isEqualTo(UPDATED_SUBV_PROJET);
        assertThat(testFicheSuiviOuvrage.getAutreSubv()).isEqualTo(UPDATED_AUTRE_SUBV);
        assertThat(testFicheSuiviOuvrage.getToles()).isEqualTo(UPDATED_TOLES);
        assertThat(testFicheSuiviOuvrage.getAnimateur()).isEqualTo(UPDATED_ANIMATEUR);
        assertThat(testFicheSuiviOuvrage.getSuperviseur()).isEqualTo(UPDATED_SUPERVISEUR);
        assertThat(testFicheSuiviOuvrage.getControleur()).isEqualTo(UPDATED_CONTROLEUR);
    }

    @Test
    void fullUpdateFicheSuiviOuvrageWithPatch() throws Exception {
        // Initialize the database
        ficheSuiviOuvrageRepository.save(ficheSuiviOuvrage).block();

        int databaseSizeBeforeUpdate = ficheSuiviOuvrageRepository.findAll().collectList().block().size();

        // Update the ficheSuiviOuvrage using partial update
        FicheSuiviOuvrage partialUpdatedFicheSuiviOuvrage = new FicheSuiviOuvrage();
        partialUpdatedFicheSuiviOuvrage.setId(ficheSuiviOuvrage.getId());

        partialUpdatedFicheSuiviOuvrage
            .prjAppuis(UPDATED_PRJ_APPUIS)
            .nomBenef(UPDATED_NOM_BENEF)
            .prenomBenef(UPDATED_PRENOM_BENEF)
            .professionBenef(UPDATED_PROFESSION_BENEF)
            .nbUsagers(UPDATED_NB_USAGERS)
            .contacts(UPDATED_CONTACTS)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .dateRemiseDevis(UPDATED_DATE_REMISE_DEVIS)
            .dateDebutTravaux(UPDATED_DATE_DEBUT_TRAVAUX)
            .dateFinTravaux(UPDATED_DATE_FIN_TRAVAUX)
            .rue(UPDATED_RUE)
            .porte(UPDATED_PORTE)
            .coutMenage(UPDATED_COUT_MENAGE)
            .subvOnea(UPDATED_SUBV_ONEA)
            .subvProjet(UPDATED_SUBV_PROJET)
            .autreSubv(UPDATED_AUTRE_SUBV)
            .toles(UPDATED_TOLES)
            .animateur(UPDATED_ANIMATEUR)
            .superviseur(UPDATED_SUPERVISEUR)
            .controleur(UPDATED_CONTROLEUR);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFicheSuiviOuvrage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFicheSuiviOuvrage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeUpdate);
        FicheSuiviOuvrage testFicheSuiviOuvrage = ficheSuiviOuvrageList.get(ficheSuiviOuvrageList.size() - 1);
        assertThat(testFicheSuiviOuvrage.getPrjAppuis()).isEqualTo(UPDATED_PRJ_APPUIS);
        assertThat(testFicheSuiviOuvrage.getNomBenef()).isEqualTo(UPDATED_NOM_BENEF);
        assertThat(testFicheSuiviOuvrage.getPrenomBenef()).isEqualTo(UPDATED_PRENOM_BENEF);
        assertThat(testFicheSuiviOuvrage.getProfessionBenef()).isEqualTo(UPDATED_PROFESSION_BENEF);
        assertThat(testFicheSuiviOuvrage.getNbUsagers()).isEqualTo(UPDATED_NB_USAGERS);
        assertThat(testFicheSuiviOuvrage.getContacts()).isEqualTo(UPDATED_CONTACTS);
        assertThat(testFicheSuiviOuvrage.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testFicheSuiviOuvrage.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testFicheSuiviOuvrage.getDateRemiseDevis()).isEqualTo(UPDATED_DATE_REMISE_DEVIS);
        assertThat(testFicheSuiviOuvrage.getDateDebutTravaux()).isEqualTo(UPDATED_DATE_DEBUT_TRAVAUX);
        assertThat(testFicheSuiviOuvrage.getDateFinTravaux()).isEqualTo(UPDATED_DATE_FIN_TRAVAUX);
        assertThat(testFicheSuiviOuvrage.getRue()).isEqualTo(UPDATED_RUE);
        assertThat(testFicheSuiviOuvrage.getPorte()).isEqualTo(UPDATED_PORTE);
        assertThat(testFicheSuiviOuvrage.getCoutMenage()).isEqualTo(UPDATED_COUT_MENAGE);
        assertThat(testFicheSuiviOuvrage.getSubvOnea()).isEqualTo(UPDATED_SUBV_ONEA);
        assertThat(testFicheSuiviOuvrage.getSubvProjet()).isEqualTo(UPDATED_SUBV_PROJET);
        assertThat(testFicheSuiviOuvrage.getAutreSubv()).isEqualTo(UPDATED_AUTRE_SUBV);
        assertThat(testFicheSuiviOuvrage.getToles()).isEqualTo(UPDATED_TOLES);
        assertThat(testFicheSuiviOuvrage.getAnimateur()).isEqualTo(UPDATED_ANIMATEUR);
        assertThat(testFicheSuiviOuvrage.getSuperviseur()).isEqualTo(UPDATED_SUPERVISEUR);
        assertThat(testFicheSuiviOuvrage.getControleur()).isEqualTo(UPDATED_CONTROLEUR);
    }

    @Test
    void patchNonExistingFicheSuiviOuvrage() throws Exception {
        int databaseSizeBeforeUpdate = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        ficheSuiviOuvrage.setId(count.incrementAndGet());

        // Create the FicheSuiviOuvrage
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, ficheSuiviOuvrageDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository, times(0)).save(ficheSuiviOuvrage);
    }

    @Test
    void patchWithIdMismatchFicheSuiviOuvrage() throws Exception {
        int databaseSizeBeforeUpdate = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        ficheSuiviOuvrage.setId(count.incrementAndGet());

        // Create the FicheSuiviOuvrage
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository, times(0)).save(ficheSuiviOuvrage);
    }

    @Test
    void patchWithMissingIdPathParamFicheSuiviOuvrage() throws Exception {
        int databaseSizeBeforeUpdate = ficheSuiviOuvrageRepository.findAll().collectList().block().size();
        ficheSuiviOuvrage.setId(count.incrementAndGet());

        // Create the FicheSuiviOuvrage
        FicheSuiviOuvrageDTO ficheSuiviOuvrageDTO = ficheSuiviOuvrageMapper.toDto(ficheSuiviOuvrage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ficheSuiviOuvrageDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the FicheSuiviOuvrage in the database
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeUpdate);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository, times(0)).save(ficheSuiviOuvrage);
    }

    @Test
    void deleteFicheSuiviOuvrage() {
        // Configure the mock search repository
        when(mockFicheSuiviOuvrageSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockFicheSuiviOuvrageSearchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        // Initialize the database
        ficheSuiviOuvrageRepository.save(ficheSuiviOuvrage).block();

        int databaseSizeBeforeDelete = ficheSuiviOuvrageRepository.findAll().collectList().block().size();

        // Delete the ficheSuiviOuvrage
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, ficheSuiviOuvrage.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<FicheSuiviOuvrage> ficheSuiviOuvrageList = ficheSuiviOuvrageRepository.findAll().collectList().block();
        assertThat(ficheSuiviOuvrageList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the FicheSuiviOuvrage in Elasticsearch
        verify(mockFicheSuiviOuvrageSearchRepository, times(1)).deleteById(ficheSuiviOuvrage.getId());
    }

    @Test
    void searchFicheSuiviOuvrage() {
        // Configure the mock search repository
        when(mockFicheSuiviOuvrageSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockFicheSuiviOuvrageSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        ficheSuiviOuvrageRepository.save(ficheSuiviOuvrage).block();
        when(mockFicheSuiviOuvrageSearchRepository.search("id:" + ficheSuiviOuvrage.getId(), PageRequest.of(0, 20)))
            .thenReturn(Flux.just(ficheSuiviOuvrage));

        // Search the ficheSuiviOuvrage
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + ficheSuiviOuvrage.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(ficheSuiviOuvrage.getId().intValue()))
            .jsonPath("$.[*].prjAppuis")
            .value(hasItem(DEFAULT_PRJ_APPUIS))
            .jsonPath("$.[*].nomBenef")
            .value(hasItem(DEFAULT_NOM_BENEF))
            .jsonPath("$.[*].prenomBenef")
            .value(hasItem(DEFAULT_PRENOM_BENEF))
            .jsonPath("$.[*].professionBenef")
            .value(hasItem(DEFAULT_PROFESSION_BENEF))
            .jsonPath("$.[*].nbUsagers")
            .value(hasItem(DEFAULT_NB_USAGERS.intValue()))
            .jsonPath("$.[*].contacts")
            .value(hasItem(DEFAULT_CONTACTS))
            .jsonPath("$.[*].longitude")
            .value(hasItem(DEFAULT_LONGITUDE.doubleValue()))
            .jsonPath("$.[*].latitude")
            .value(hasItem(DEFAULT_LATITUDE.doubleValue()))
            .jsonPath("$.[*].dateRemiseDevis")
            .value(hasItem(DEFAULT_DATE_REMISE_DEVIS.toString()))
            .jsonPath("$.[*].dateDebutTravaux")
            .value(hasItem(DEFAULT_DATE_DEBUT_TRAVAUX.toString()))
            .jsonPath("$.[*].dateFinTravaux")
            .value(hasItem(DEFAULT_DATE_FIN_TRAVAUX.toString()))
            .jsonPath("$.[*].rue")
            .value(hasItem(DEFAULT_RUE))
            .jsonPath("$.[*].porte")
            .value(hasItem(DEFAULT_PORTE))
            .jsonPath("$.[*].coutMenage")
            .value(hasItem(DEFAULT_COUT_MENAGE))
            .jsonPath("$.[*].subvOnea")
            .value(hasItem(DEFAULT_SUBV_ONEA))
            .jsonPath("$.[*].subvProjet")
            .value(hasItem(DEFAULT_SUBV_PROJET))
            .jsonPath("$.[*].autreSubv")
            .value(hasItem(DEFAULT_AUTRE_SUBV))
            .jsonPath("$.[*].toles")
            .value(hasItem(DEFAULT_TOLES))
            .jsonPath("$.[*].animateur")
            .value(hasItem(DEFAULT_ANIMATEUR))
            .jsonPath("$.[*].superviseur")
            .value(hasItem(DEFAULT_SUPERVISEUR))
            .jsonPath("$.[*].controleur")
            .value(hasItem(DEFAULT_CONTROLEUR));
    }
}
