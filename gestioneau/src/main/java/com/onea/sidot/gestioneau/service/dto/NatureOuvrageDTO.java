package com.onea.sidot.gestioneau.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.onea.sidot.gestioneau.domain.NatureOuvrage} entity.
 */
public class NatureOuvrageDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String libelle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NatureOuvrageDTO)) {
            return false;
        }

        NatureOuvrageDTO natureOuvrageDTO = (NatureOuvrageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, natureOuvrageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NatureOuvrageDTO{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            "}";
    }
}
