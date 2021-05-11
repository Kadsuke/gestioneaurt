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
 * A Commune.
 */
@Table("commune")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "commune")
public class Commune implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("libelle")
    private String libelle;

    @JsonIgnoreProperties(value = { "region" }, allowSetters = true)
    @Transient
    private Province province;

    @Column("province_id")
    private Long provinceId;

    @Transient
    private TypeCommune typecommune;

    @Column("typecommune_id")
    private Long typecommuneId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Commune id(Long id) {
        this.id = id;
        return this;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Commune libelle(String libelle) {
        this.libelle = libelle;
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Province getProvince() {
        return this.province;
    }

    public Commune province(Province province) {
        this.setProvince(province);
        this.provinceId = province != null ? province.getId() : null;
        return this;
    }

    public void setProvince(Province province) {
        this.province = province;
        this.provinceId = province != null ? province.getId() : null;
    }

    public Long getProvinceId() {
        return this.provinceId;
    }

    public void setProvinceId(Long province) {
        this.provinceId = province;
    }

    public TypeCommune getTypecommune() {
        return this.typecommune;
    }

    public Commune typecommune(TypeCommune typeCommune) {
        this.setTypecommune(typeCommune);
        this.typecommuneId = typeCommune != null ? typeCommune.getId() : null;
        return this;
    }

    public void setTypecommune(TypeCommune typeCommune) {
        this.typecommune = typeCommune;
        this.typecommuneId = typeCommune != null ? typeCommune.getId() : null;
    }

    public Long getTypecommuneId() {
        return this.typecommuneId;
    }

    public void setTypecommuneId(Long typeCommune) {
        this.typecommuneId = typeCommune;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Commune)) {
            return false;
        }
        return id != null && id.equals(((Commune) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Commune{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            "}";
    }
}
