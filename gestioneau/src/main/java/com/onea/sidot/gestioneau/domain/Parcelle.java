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
 * A Parcelle.
 */
@Table("parcelle")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "parcelle")
public class Parcelle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("libelle")
    private String libelle;

    @JsonIgnoreProperties(value = { "section" }, allowSetters = true)
    @Transient
    private Lot lot;

    @Column("lot_id")
    private Long lotId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Parcelle id(Long id) {
        this.id = id;
        return this;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Parcelle libelle(String libelle) {
        this.libelle = libelle;
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Lot getLot() {
        return this.lot;
    }

    public Parcelle lot(Lot lot) {
        this.setLot(lot);
        this.lotId = lot != null ? lot.getId() : null;
        return this;
    }

    public void setLot(Lot lot) {
        this.lot = lot;
        this.lotId = lot != null ? lot.getId() : null;
    }

    public Long getLotId() {
        return this.lotId;
    }

    public void setLotId(Long lot) {
        this.lotId = lot;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Parcelle)) {
            return false;
        }
        return id != null && id.equals(((Parcelle) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Parcelle{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            "}";
    }
}
