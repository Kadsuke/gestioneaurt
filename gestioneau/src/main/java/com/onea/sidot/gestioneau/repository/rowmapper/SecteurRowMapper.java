package com.onea.sidot.gestioneau.repository.rowmapper;

import com.onea.sidot.gestioneau.domain.Secteur;
import com.onea.sidot.gestioneau.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Secteur}, with proper type conversions.
 */
@Service
public class SecteurRowMapper implements BiFunction<Row, String, Secteur> {

    private final ColumnConverter converter;

    public SecteurRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Secteur} stored in the database.
     */
    @Override
    public Secteur apply(Row row, String prefix) {
        Secteur entity = new Secteur();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLibelle(converter.fromRow(row, prefix + "_libelle", String.class));
        entity.setLocaliteId(converter.fromRow(row, prefix + "_localite_id", Long.class));
        return entity;
    }
}
