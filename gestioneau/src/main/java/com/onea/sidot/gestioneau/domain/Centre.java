package com.onea.sidot.gestioneau.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Centre.
 */
@Table("centre")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "centre")
public class Centre implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("libelle")
    private String libelle;

    @NotNull(message = "must not be null")
    @Column("responsable")
    private String responsable;

    @NotNull(message = "must not be null")
    @Column("contact")
    private String contact;

    @JsonIgnoreProperties(value = { "directionregionale" }, allowSetters = true)
    @Transient
    private CentreRegroupement centreregroupement;

    @Column("centreregroupement_id")
    private Long centreregroupementId;

    @Transient
    private Prevision prevision;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Centre id(Long id) {
        this.id = id;
        return this;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Centre libelle(String libelle) {
        this.libelle = libelle;
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getResponsable() {
        return this.responsable;
    }

    public Centre responsable(String responsable) {
        this.responsable = responsable;
        return this;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getContact() {
        return this.contact;
    }

    public Centre contact(String contact) {
        this.contact = contact;
        return this;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public CentreRegroupement getCentreregroupement() {
        return this.centreregroupement;
    }

    public Centre centreregroupement(CentreRegroupement centreRegroupement) {
        this.setCentreregroupement(centreRegroupement);
        this.centreregroupementId = centreRegroupement != null ? centreRegroupement.getId() : null;
        return this;
    }

    public void setCentreregroupement(CentreRegroupement centreRegroupement) {
        this.centreregroupement = centreRegroupement;
        this.centreregroupementId = centreRegroupement != null ? centreRegroupement.getId() : null;
    }

    public Long getCentreregroupementId() {
        return this.centreregroupementId;
    }

    public void setCentreregroupementId(Long centreRegroupement) {
        this.centreregroupementId = centreRegroupement;
    }

    public Prevision getPrevision() {
        return this.prevision;
    }

    public Centre prevision(Prevision prevision) {
        this.setPrevision(prevision);
        return this;
    }

    public void setPrevision(Prevision prevision) {
        if (this.prevision != null) {
            this.prevision.setCentre(null);
        }
        if (prevision != null) {
            prevision.setCentre(this);
        }
        this.prevision = prevision;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Centre)) {
            return false;
        }
        return id != null && id.equals(((Centre) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Centre{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            ", responsable='" + getResponsable() + "'" +
            ", contact='" + getContact() + "'" +
            "}";
    }
}
