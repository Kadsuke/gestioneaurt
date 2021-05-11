package com.onea.sidot.gestioneau.repository.rowmapper;

import com.onea.sidot.gestioneau.domain.Centre;
import com.onea.sidot.gestioneau.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Centre}, with proper type conversions.
 */
@Service
public class CentreRowMapper implements BiFunction<Row, String, Centre> {

    private final ColumnConverter converter;

    public CentreRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Centre} stored in the database.
     */
    @Override
    public Centre apply(Row row, String prefix) {
        Centre entity = new Centre();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLibelle(converter.fromRow(row, prefix + "_libelle", String.class));
        entity.setResponsable(converter.fromRow(row, prefix + "_responsable", String.class));
        entity.setContact(converter.fromRow(row, prefix + "_contact", String.class));
        entity.setCentreregroupementId(converter.fromRow(row, prefix + "_centreregroupement_id", Long.class));
        return entity;
    }
}
