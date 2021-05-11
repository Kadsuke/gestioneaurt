package com.onea.sidot.gestioneau.repository.rowmapper;

import com.onea.sidot.gestioneau.domain.FicheSuiviOuvrage;
import com.onea.sidot.gestioneau.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link FicheSuiviOuvrage}, with proper type conversions.
 */
@Service
public class FicheSuiviOuvrageRowMapper implements BiFunction<Row, String, FicheSuiviOuvrage> {

    private final ColumnConverter converter;

    public FicheSuiviOuvrageRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link FicheSuiviOuvrage} stored in the database.
     */
    @Override
    public FicheSuiviOuvrage apply(Row row, String prefix) {
        FicheSuiviOuvrage entity = new FicheSuiviOuvrage();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPrjAppuis(converter.fromRow(row, prefix + "_prj_appuis", String.class));
        entity.setNomBenef(converter.fromRow(row, prefix + "_nom_benef", String.class));
        entity.setPrenomBenef(converter.fromRow(row, prefix + "_prenom_benef", String.class));
        entity.setProfessionBenef(converter.fromRow(row, prefix + "_profession_benef", String.class));
        entity.setNbUsagers(converter.fromRow(row, prefix + "_nb_usagers", Long.class));
        entity.setContacts(converter.fromRow(row, prefix + "_contacts", String.class));
        entity.setLongitude(converter.fromRow(row, prefix + "_longitude", Float.class));
        entity.setLatitude(converter.fromRow(row, prefix + "_latitude", Float.class));
        entity.setDateRemiseDevis(converter.fromRow(row, prefix + "_date_remise_devis", Instant.class));
        entity.setDateDebutTravaux(converter.fromRow(row, prefix + "_date_debut_travaux", Instant.class));
        entity.setDateFinTravaux(converter.fromRow(row, prefix + "_date_fin_travaux", Instant.class));
        entity.setRue(converter.fromRow(row, prefix + "_rue", String.class));
        entity.setPorte(converter.fromRow(row, prefix + "_porte", String.class));
        entity.setCoutMenage(converter.fromRow(row, prefix + "_cout_menage", String.class));
        entity.setSubvOnea(converter.fromRow(row, prefix + "_subv_onea", Integer.class));
        entity.setSubvProjet(converter.fromRow(row, prefix + "_subv_projet", Integer.class));
        entity.setAutreSubv(converter.fromRow(row, prefix + "_autre_subv", Integer.class));
        entity.setToles(converter.fromRow(row, prefix + "_toles", Integer.class));
        entity.setAnimateur(converter.fromRow(row, prefix + "_animateur", String.class));
        entity.setSuperviseur(converter.fromRow(row, prefix + "_superviseur", String.class));
        entity.setControleur(converter.fromRow(row, prefix + "_controleur", String.class));
        entity.setParcelleId(converter.fromRow(row, prefix + "_parcelle_id", Long.class));
        entity.setPrevisionId(converter.fromRow(row, prefix + "_prevision_id", Long.class));
        entity.setNatureouvrageId(converter.fromRow(row, prefix + "_natureouvrage_id", Long.class));
        entity.setTypehabitationId(converter.fromRow(row, prefix + "_typehabitation_id", Long.class));
        entity.setSourceapprovepId(converter.fromRow(row, prefix + "_sourceapprovep_id", Long.class));
        entity.setModeevacuationeauuseeId(converter.fromRow(row, prefix + "_modeevacuationeauusee_id", Long.class));
        entity.setModeevacexcretaId(converter.fromRow(row, prefix + "_modeevacexcreta_id", Long.class));
        entity.setMaconId(converter.fromRow(row, prefix + "_macon_id", Long.class));
        entity.setPrefabricantId(converter.fromRow(row, prefix + "_prefabricant_id", Long.class));
        return entity;
    }
}
