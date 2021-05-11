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
 * A Secteur.
 */
@Table("secteur")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "secteur")
public class Secteur implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("libelle")
    private String libelle;

    @JsonIgnoreProperties(value = { "commune" }, allowSetters = true)
    @Transient
    private Localite localite;

    @Column("localite_id")
    private Long localiteId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Secteur id(Long id) {
        this.id = id;
        return this;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Secteur libelle(String libelle) {
        this.libelle = libelle;
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Localite getLocalite() {
        return this.localite;
    }

    public Secteur localite(Localite localite) {
        this.setLocalite(localite);
        this.localiteId = localite != null ? localite.getId() : null;
        return this;
    }

    public void setLocalite(Localite localite) {
        this.localite = localite;
        this.localiteId = localite != null ? localite.getId() : null;
    }

    public Long getLocaliteId() {
        return this.localiteId;
    }

    public void setLocaliteId(Long localite) {
        this.localiteId = localite;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Secteur)) {
            return false;
        }
        return id != null && id.equals(((Secteur) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Secteur{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            "}";
    }
}
