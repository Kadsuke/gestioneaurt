package com.onea.sidot.gestioneau.repository.rowmapper;

import com.onea.sidot.gestioneau.domain.Prevision;
import com.onea.sidot.gestioneau.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Prevision}, with proper type conversions.
 */
@Service
public class PrevisionRowMapper implements BiFunction<Row, String, Prevision> {

    private final ColumnConverter converter;

    public PrevisionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Prevision} stored in the database.
     */
    @Override
    public Prevision apply(Row row, String prefix) {
        Prevision entity = new Prevision();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNbLatrine(converter.fromRow(row, prefix + "_nb_latrine", Integer.class));
        entity.setNbPuisard(converter.fromRow(row, prefix + "_nb_puisard", Integer.class));
        entity.setNbPublic(converter.fromRow(row, prefix + "_nb_public", Integer.class));
        entity.setNbScolaire(converter.fromRow(row, prefix + "_nb_scolaire", Integer.class));
        entity.setCentreId(converter.fromRow(row, prefix + "_centre_id", Long.class));
        entity.setRefanneeId(converter.fromRow(row, prefix + "_refannee_id", Long.class));
        return entity;
    }
}
