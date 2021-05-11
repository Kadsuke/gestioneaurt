package com.onea.sidot.gestioneau.domain;

import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Province.
 */
@Table("province")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "province")
public class Province implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("libelle")
    private String libelle;

    @Transient
    private Region region;

    @Column("region_id")
    private Long regionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Province id(Long id) {
        this.id = id;
        return this;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Province libelle(String libelle) {
        this.libelle = libelle;
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Region getRegion() {
        return this.region;
    }

    public Province region(Region region) {
        this.setRegion(region);
        this.regionId = region != null ? region.getId() : null;
        return this;
    }

    public void setRegion(Region region) {
        this.region = region;
        this.regionId = region != null ? region.getId() : null;
    }

    public Long getRegionId() {
        return this.regionId;
    }

    public void setRegionId(Long region) {
        this.regionId = region;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Province)) {
            return false;
        }
        return id != null && id.equals(((Province) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Province{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            "}";
    }
}
