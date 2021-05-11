package com.onea.sidot.gestioneau.repository.rowmapper;

import com.onea.sidot.gestioneau.domain.Localite;
import com.onea.sidot.gestioneau.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Localite}, with proper type conversions.
 */
@Service
public class LocaliteRowMapper implements BiFunction<Row, String, Localite> {

    private final ColumnConverter converter;

    public LocaliteRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Localite} stored in the database.
     */
    @Override
    public Localite apply(Row row, String prefix) {
        Localite entity = new Localite();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLibelle(converter.fromRow(row, prefix + "_libelle", String.class));
        entity.setCommuneId(converter.fromRow(row, prefix + "_commune_id", Long.class));
        return entity;
    }
}
