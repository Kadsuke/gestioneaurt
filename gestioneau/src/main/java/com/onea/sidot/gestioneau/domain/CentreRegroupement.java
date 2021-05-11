package com.onea.sidot.gestioneau.domain;

import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A CentreRegroupement.
 */
@Table("centre_regroupement")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "centreregroupement")
public class CentreRegroupement implements Serializable {

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

    @Transient
    private DirectionRegionale directionregionale;

    @Column("directionregionale_id")
    private Long directionregionaleId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CentreRegroupement id(Long id) {
        this.id = id;
        return this;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public CentreRegroupement libelle(String libelle) {
        this.libelle = libelle;
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getResponsable() {
        return this.responsable;
    }

    public CentreRegroupement responsable(String responsable) {
        this.responsable = responsable;
        return this;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getContact() {
        return this.contact;
    }

    public CentreRegroupement contact(String contact) {
        this.contact = contact;
        return this;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public DirectionRegionale getDirectionregionale() {
        return this.directionregionale;
    }

    public CentreRegroupement directionregionale(DirectionRegionale directionRegionale) {
        this.setDirectionregionale(directionRegionale);
        this.directionregionaleId = directionRegionale != null ? directionRegionale.getId() : null;
        return this;
    }

    public void setDirectionregionale(DirectionRegionale directionRegionale) {
        this.directionregionale = directionRegionale;
        this.directionregionaleId = directionRegionale != null ? directionRegionale.getId() : null;
    }

    public Long getDirectionregionaleId() {
        return this.directionregionaleId;
    }

    public void setDirectionregionaleId(Long directionRegionale) {
        this.directionregionaleId = directionRegionale;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CentreRegroupement)) {
            return false;
        }
        return id != null && id.equals(((CentreRegroupement) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CentreRegroupement{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            ", responsable='" + getResponsable() + "'" +
            ", contact='" + getContact() + "'" +
            "}";
    }
}
