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
 * A Prevision.
 */
@Table("prevision")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "prevision")
public class Prevision implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("nb_latrine")
    private Integer nbLatrine;

    @NotNull(message = "must not be null")
    @Column("nb_puisard")
    private Integer nbPuisard;

    @NotNull(message = "must not be null")
    @Column("nb_public")
    private Integer nbPublic;

    @NotNull(message = "must not be null")
    @Column("nb_scolaire")
    private Integer nbScolaire;

    private Long centreId;

    @Transient
    private Centre centre;

    private Long refanneeId;

    @Transient
    private Annee refannee;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Prevision id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getNbLatrine() {
        return this.nbLatrine;
    }

    public Prevision nbLatrine(Integer nbLatrine) {
        this.nbLatrine = nbLatrine;
        return this;
    }

    public void setNbLatrine(Integer nbLatrine) {
        this.nbLatrine = nbLatrine;
    }

    public Integer getNbPuisard() {
        return this.nbPuisard;
    }

    public Prevision nbPuisard(Integer nbPuisard) {
        this.nbPuisard = nbPuisard;
        return this;
    }

    public void setNbPuisard(Integer nbPuisard) {
        this.nbPuisard = nbPuisard;
    }

    public Integer getNbPublic() {
        return this.nbPublic;
    }

    public Prevision nbPublic(Integer nbPublic) {
        this.nbPublic = nbPublic;
        return this;
    }

    public void setNbPublic(Integer nbPublic) {
        this.nbPublic = nbPublic;
    }

    public Integer getNbScolaire() {
        return this.nbScolaire;
    }

    public Prevision nbScolaire(Integer nbScolaire) {
        this.nbScolaire = nbScolaire;
        return this;
    }

    public void setNbScolaire(Integer nbScolaire) {
        this.nbScolaire = nbScolaire;
    }

    public Centre getCentre() {
        return this.centre;
    }

    public Prevision centre(Centre centre) {
        this.setCentre(centre);
        this.centreId = centre != null ? centre.getId() : null;
        return this;
    }

    public void setCentre(Centre centre) {
        this.centre = centre;
        this.centreId = centre != null ? centre.getId() : null;
    }

    public Long getCentreId() {
        return this.centreId;
    }

    public void setCentreId(Long centre) {
        this.centreId = centre;
    }

    public Annee getRefannee() {
        return this.refannee;
    }

    public Prevision refannee(Annee annee) {
        this.setRefannee(annee);
        this.refanneeId = annee != null ? annee.getId() : null;
        return this;
    }

    public void setRefannee(Annee annee) {
        this.refannee = annee;
        this.refanneeId = annee != null ? annee.getId() : null;
    }

    public Long getRefanneeId() {
        return this.refanneeId;
    }

    public void setRefanneeId(Long annee) {
        this.refanneeId = annee;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Prevision)) {
            return false;
        }
        return id != null && id.equals(((Prevision) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Prevision{" +
            "id=" + getId() +
            ", nbLatrine=" + getNbLatrine() +
            ", nbPuisard=" + getNbPuisard() +
            ", nbPublic=" + getNbPublic() +
            ", nbScolaire=" + getNbScolaire() +
            "}";
    }
}
