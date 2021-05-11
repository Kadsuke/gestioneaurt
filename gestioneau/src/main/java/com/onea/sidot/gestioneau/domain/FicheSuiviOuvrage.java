package com.onea.sidot.gestioneau.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A FicheSuiviOuvrage.
 */
@Table("fiche_suivi_ouvrage")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "fichesuiviouvrage")
public class FicheSuiviOuvrage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("prj_appuis")
    private String prjAppuis;

    @NotNull(message = "must not be null")
    @Column("nom_benef")
    private String nomBenef;

    @NotNull(message = "must not be null")
    @Column("prenom_benef")
    private String prenomBenef;

    @NotNull(message = "must not be null")
    @Column("profession_benef")
    private String professionBenef;

    @NotNull(message = "must not be null")
    @Column("nb_usagers")
    private Long nbUsagers;

    @NotNull(message = "must not be null")
    @Column("contacts")
    private String contacts;

    @NotNull(message = "must not be null")
    @Column("longitude")
    private Float longitude;

    @NotNull(message = "must not be null")
    @Column("latitude")
    private Float latitude;

    @NotNull(message = "must not be null")
    @Column("date_remise_devis")
    private Instant dateRemiseDevis;

    @NotNull(message = "must not be null")
    @Column("date_debut_travaux")
    private Instant dateDebutTravaux;

    @NotNull(message = "must not be null")
    @Column("date_fin_travaux")
    private Instant dateFinTravaux;

    @Column("rue")
    private String rue;

    @Column("porte")
    private String porte;

    @NotNull(message = "must not be null")
    @Column("cout_menage")
    private String coutMenage;

    @NotNull(message = "must not be null")
    @Column("subv_onea")
    private Integer subvOnea;

    @NotNull(message = "must not be null")
    @Column("subv_projet")
    private Integer subvProjet;

    @NotNull(message = "must not be null")
    @Column("autre_subv")
    private Integer autreSubv;

    @NotNull(message = "must not be null")
    @Column("toles")
    private Integer toles;

    @NotNull(message = "must not be null")
    @Column("animateur")
    private String animateur;

    @NotNull(message = "must not be null")
    @Column("superviseur")
    private String superviseur;

    @NotNull(message = "must not be null")
    @Column("controleur")
    private String controleur;

    @JsonIgnoreProperties(value = { "lot" }, allowSetters = true)
    @Transient
    private Parcelle parcelle;

    @Column("parcelle_id")
    private Long parcelleId;

    @JsonIgnoreProperties(value = { "centre", "refannee" }, allowSetters = true)
    @Transient
    private Prevision prevision;

    @Column("prevision_id")
    private Long previsionId;

    @Transient
    private NatureOuvrage natureouvrage;

    @Column("natureouvrage_id")
    private Long natureouvrageId;

    @Transient
    private TypeHabitation typehabitation;

    @Column("typehabitation_id")
    private Long typehabitationId;

    @Transient
    private SourceApprovEp sourceapprovep;

    @Column("sourceapprovep_id")
    private Long sourceapprovepId;

    @Transient
    private ModeEvacuationEauUsee modeevacuationeauusee;

    @Column("modeevacuationeauusee_id")
    private Long modeevacuationeauuseeId;

    @Transient
    private ModeEvacExcreta modeevacexcreta;

    @Column("modeevacexcreta_id")
    private Long modeevacexcretaId;

    @Transient
    private Macon macon;

    @Column("macon_id")
    private Long maconId;

    @Transient
    private Prefabricant prefabricant;

    @Column("prefabricant_id")
    private Long prefabricantId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FicheSuiviOuvrage id(Long id) {
        this.id = id;
        return this;
    }

    public String getPrjAppuis() {
        return this.prjAppuis;
    }

    public FicheSuiviOuvrage prjAppuis(String prjAppuis) {
        this.prjAppuis = prjAppuis;
        return this;
    }

    public void setPrjAppuis(String prjAppuis) {
        this.prjAppuis = prjAppuis;
    }

    public String getNomBenef() {
        return this.nomBenef;
    }

    public FicheSuiviOuvrage nomBenef(String nomBenef) {
        this.nomBenef = nomBenef;
        return this;
    }

    public void setNomBenef(String nomBenef) {
        this.nomBenef = nomBenef;
    }

    public String getPrenomBenef() {
        return this.prenomBenef;
    }

    public FicheSuiviOuvrage prenomBenef(String prenomBenef) {
        this.prenomBenef = prenomBenef;
        return this;
    }

    public void setPrenomBenef(String prenomBenef) {
        this.prenomBenef = prenomBenef;
    }

    public String getProfessionBenef() {
        return this.professionBenef;
    }

    public FicheSuiviOuvrage professionBenef(String professionBenef) {
        this.professionBenef = professionBenef;
        return this;
    }

    public void setProfessionBenef(String professionBenef) {
        this.professionBenef = professionBenef;
    }

    public Long getNbUsagers() {
        return this.nbUsagers;
    }

    public FicheSuiviOuvrage nbUsagers(Long nbUsagers) {
        this.nbUsagers = nbUsagers;
        return this;
    }

    public void setNbUsagers(Long nbUsagers) {
        this.nbUsagers = nbUsagers;
    }

    public String getContacts() {
        return this.contacts;
    }

    public FicheSuiviOuvrage contacts(String contacts) {
        this.contacts = contacts;
        return this;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public Float getLongitude() {
        return this.longitude;
    }

    public FicheSuiviOuvrage longitude(Float longitude) {
        this.longitude = longitude;
        return this;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return this.latitude;
    }

    public FicheSuiviOuvrage latitude(Float latitude) {
        this.latitude = latitude;
        return this;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Instant getDateRemiseDevis() {
        return this.dateRemiseDevis;
    }

    public FicheSuiviOuvrage dateRemiseDevis(Instant dateRemiseDevis) {
        this.dateRemiseDevis = dateRemiseDevis;
        return this;
    }

    public void setDateRemiseDevis(Instant dateRemiseDevis) {
        this.dateRemiseDevis = dateRemiseDevis;
    }

    public Instant getDateDebutTravaux() {
        return this.dateDebutTravaux;
    }

    public FicheSuiviOuvrage dateDebutTravaux(Instant dateDebutTravaux) {
        this.dateDebutTravaux = dateDebutTravaux;
        return this;
    }

    public void setDateDebutTravaux(Instant dateDebutTravaux) {
        this.dateDebutTravaux = dateDebutTravaux;
    }

    public Instant getDateFinTravaux() {
        return this.dateFinTravaux;
    }

    public FicheSuiviOuvrage dateFinTravaux(Instant dateFinTravaux) {
        this.dateFinTravaux = dateFinTravaux;
        return this;
    }

    public void setDateFinTravaux(Instant dateFinTravaux) {
        this.dateFinTravaux = dateFinTravaux;
    }

    public String getRue() {
        return this.rue;
    }

    public FicheSuiviOuvrage rue(String rue) {
        this.rue = rue;
        return this;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public String getPorte() {
        return this.porte;
    }

    public FicheSuiviOuvrage porte(String porte) {
        this.porte = porte;
        return this;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public String getCoutMenage() {
        return this.coutMenage;
    }

    public FicheSuiviOuvrage coutMenage(String coutMenage) {
        this.coutMenage = coutMenage;
        return this;
    }

    public void setCoutMenage(String coutMenage) {
        this.coutMenage = coutMenage;
    }

    public Integer getSubvOnea() {
        return this.subvOnea;
    }

    public FicheSuiviOuvrage subvOnea(Integer subvOnea) {
        this.subvOnea = subvOnea;
        return this;
    }

    public void setSubvOnea(Integer subvOnea) {
        this.subvOnea = subvOnea;
    }

    public Integer getSubvProjet() {
        return this.subvProjet;
    }

    public FicheSuiviOuvrage subvProjet(Integer subvProjet) {
        this.subvProjet = subvProjet;
        return this;
    }

    public void setSubvProjet(Integer subvProjet) {
        this.subvProjet = subvProjet;
    }

    public Integer getAutreSubv() {
        return this.autreSubv;
    }

    public FicheSuiviOuvrage autreSubv(Integer autreSubv) {
        this.autreSubv = autreSubv;
        return this;
    }

    public void setAutreSubv(Integer autreSubv) {
        this.autreSubv = autreSubv;
    }

    public Integer getToles() {
        return this.toles;
    }

    public FicheSuiviOuvrage toles(Integer toles) {
        this.toles = toles;
        return this;
    }

    public void setToles(Integer toles) {
        this.toles = toles;
    }

    public String getAnimateur() {
        return this.animateur;
    }

    public FicheSuiviOuvrage animateur(String animateur) {
        this.animateur = animateur;
        return this;
    }

    public void setAnimateur(String animateur) {
        this.animateur = animateur;
    }

    public String getSuperviseur() {
        return this.superviseur;
    }

    public FicheSuiviOuvrage superviseur(String superviseur) {
        this.superviseur = superviseur;
        return this;
    }

    public void setSuperviseur(String superviseur) {
        this.superviseur = superviseur;
    }

    public String getControleur() {
        return this.controleur;
    }

    public FicheSuiviOuvrage controleur(String controleur) {
        this.controleur = controleur;
        return this;
    }

    public void setControleur(String controleur) {
        this.controleur = controleur;
    }

    public Parcelle getParcelle() {
        return this.parcelle;
    }

    public FicheSuiviOuvrage parcelle(Parcelle parcelle) {
        this.setParcelle(parcelle);
        this.parcelleId = parcelle != null ? parcelle.getId() : null;
        return this;
    }

    public void setParcelle(Parcelle parcelle) {
        this.parcelle = parcelle;
        this.parcelleId = parcelle != null ? parcelle.getId() : null;
    }

    public Long getParcelleId() {
        return this.parcelleId;
    }

    public void setParcelleId(Long parcelle) {
        this.parcelleId = parcelle;
    }

    public Prevision getPrevision() {
        return this.prevision;
    }

    public FicheSuiviOuvrage prevision(Prevision prevision) {
        this.setPrevision(prevision);
        this.previsionId = prevision != null ? prevision.getId() : null;
        return this;
    }

    public void setPrevision(Prevision prevision) {
        this.prevision = prevision;
        this.previsionId = prevision != null ? prevision.getId() : null;
    }

    public Long getPrevisionId() {
        return this.previsionId;
    }

    public void setPrevisionId(Long prevision) {
        this.previsionId = prevision;
    }

    public NatureOuvrage getNatureouvrage() {
        return this.natureouvrage;
    }

    public FicheSuiviOuvrage natureouvrage(NatureOuvrage natureOuvrage) {
        this.setNatureouvrage(natureOuvrage);
        this.natureouvrageId = natureOuvrage != null ? natureOuvrage.getId() : null;
        return this;
    }

    public void setNatureouvrage(NatureOuvrage natureOuvrage) {
        this.natureouvrage = natureOuvrage;
        this.natureouvrageId = natureOuvrage != null ? natureOuvrage.getId() : null;
    }

    public Long getNatureouvrageId() {
        return this.natureouvrageId;
    }

    public void setNatureouvrageId(Long natureOuvrage) {
        this.natureouvrageId = natureOuvrage;
    }

    public TypeHabitation getTypehabitation() {
        return this.typehabitation;
    }

    public FicheSuiviOuvrage typehabitation(TypeHabitation typeHabitation) {
        this.setTypehabitation(typeHabitation);
        this.typehabitationId = typeHabitation != null ? typeHabitation.getId() : null;
        return this;
    }

    public void setTypehabitation(TypeHabitation typeHabitation) {
        this.typehabitation = typeHabitation;
        this.typehabitationId = typeHabitation != null ? typeHabitation.getId() : null;
    }

    public Long getTypehabitationId() {
        return this.typehabitationId;
    }

    public void setTypehabitationId(Long typeHabitation) {
        this.typehabitationId = typeHabitation;
    }

    public SourceApprovEp getSourceapprovep() {
        return this.sourceapprovep;
    }

    public FicheSuiviOuvrage sourceapprovep(SourceApprovEp sourceApprovEp) {
        this.setSourceapprovep(sourceApprovEp);
        this.sourceapprovepId = sourceApprovEp != null ? sourceApprovEp.getId() : null;
        return this;
    }

    public void setSourceapprovep(SourceApprovEp sourceApprovEp) {
        this.sourceapprovep = sourceApprovEp;
        this.sourceapprovepId = sourceApprovEp != null ? sourceApprovEp.getId() : null;
    }

    public Long getSourceapprovepId() {
        return this.sourceapprovepId;
    }

    public void setSourceapprovepId(Long sourceApprovEp) {
        this.sourceapprovepId = sourceApprovEp;
    }

    public ModeEvacuationEauUsee getModeevacuationeauusee() {
        return this.modeevacuationeauusee;
    }

    public FicheSuiviOuvrage modeevacuationeauusee(ModeEvacuationEauUsee modeEvacuationEauUsee) {
        this.setModeevacuationeauusee(modeEvacuationEauUsee);
        this.modeevacuationeauuseeId = modeEvacuationEauUsee != null ? modeEvacuationEauUsee.getId() : null;
        return this;
    }

    public void setModeevacuationeauusee(ModeEvacuationEauUsee modeEvacuationEauUsee) {
        this.modeevacuationeauusee = modeEvacuationEauUsee;
        this.modeevacuationeauuseeId = modeEvacuationEauUsee != null ? modeEvacuationEauUsee.getId() : null;
    }

    public Long getModeevacuationeauuseeId() {
        return this.modeevacuationeauuseeId;
    }

    public void setModeevacuationeauuseeId(Long modeEvacuationEauUsee) {
        this.modeevacuationeauuseeId = modeEvacuationEauUsee;
    }

    public ModeEvacExcreta getModeevacexcreta() {
        return this.modeevacexcreta;
    }

    public FicheSuiviOuvrage modeevacexcreta(ModeEvacExcreta modeEvacExcreta) {
        this.setModeevacexcreta(modeEvacExcreta);
        this.modeevacexcretaId = modeEvacExcreta != null ? modeEvacExcreta.getId() : null;
        return this;
    }

    public void setModeevacexcreta(ModeEvacExcreta modeEvacExcreta) {
        this.modeevacexcreta = modeEvacExcreta;
        this.modeevacexcretaId = modeEvacExcreta != null ? modeEvacExcreta.getId() : null;
    }

    public Long getModeevacexcretaId() {
        return this.modeevacexcretaId;
    }

    public void setModeevacexcretaId(Long modeEvacExcreta) {
        this.modeevacexcretaId = modeEvacExcreta;
    }

    public Macon getMacon() {
        return this.macon;
    }

    public FicheSuiviOuvrage macon(Macon macon) {
        this.setMacon(macon);
        this.maconId = macon != null ? macon.getId() : null;
        return this;
    }

    public void setMacon(Macon macon) {
        this.macon = macon;
        this.maconId = macon != null ? macon.getId() : null;
    }

    public Long getMaconId() {
        return this.maconId;
    }

    public void setMaconId(Long macon) {
        this.maconId = macon;
    }

    public Prefabricant getPrefabricant() {
        return this.prefabricant;
    }

    public FicheSuiviOuvrage prefabricant(Prefabricant prefabricant) {
        this.setPrefabricant(prefabricant);
        this.prefabricantId = prefabricant != null ? prefabricant.getId() : null;
        return this;
    }

    public void setPrefabricant(Prefabricant prefabricant) {
        this.prefabricant = prefabricant;
        this.prefabricantId = prefabricant != null ? prefabricant.getId() : null;
    }

    public Long getPrefabricantId() {
        return this.prefabricantId;
    }

    public void setPrefabricantId(Long prefabricant) {
        this.prefabricantId = prefabricant;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FicheSuiviOuvrage)) {
            return false;
        }
        return id != null && id.equals(((FicheSuiviOuvrage) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FicheSuiviOuvrage{" +
            "id=" + getId() +
            ", prjAppuis='" + getPrjAppuis() + "'" +
            ", nomBenef='" + getNomBenef() + "'" +
            ", prenomBenef='" + getPrenomBenef() + "'" +
            ", professionBenef='" + getProfessionBenef() + "'" +
            ", nbUsagers=" + getNbUsagers() +
            ", contacts='" + getContacts() + "'" +
            ", longitude=" + getLongitude() +
            ", latitude=" + getLatitude() +
            ", dateRemiseDevis='" + getDateRemiseDevis() + "'" +
            ", dateDebutTravaux='" + getDateDebutTravaux() + "'" +
            ", dateFinTravaux='" + getDateFinTravaux() + "'" +
            ", rue='" + getRue() + "'" +
            ", porte='" + getPorte() + "'" +
            ", coutMenage='" + getCoutMenage() + "'" +
            ", subvOnea=" + getSubvOnea() +
            ", subvProjet=" + getSubvProjet() +
            ", autreSubv=" + getAutreSubv() +
            ", toles=" + getToles() +
            ", animateur='" + getAnimateur() + "'" +
            ", superviseur='" + getSuperviseur() + "'" +
            ", controleur='" + getControleur() + "'" +
            "}";
    }
}
