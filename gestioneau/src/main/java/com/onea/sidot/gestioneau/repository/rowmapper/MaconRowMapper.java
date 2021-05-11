package com.onea.sidot.gestioneau.repository.rowmapper;

import com.onea.sidot.gestioneau.domain.Macon;
import com.onea.sidot.gestioneau.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Macon}, with proper type conversions.
 */
@Service
public class MaconRowMapper implements BiFunction<Row, String, Macon> {

    private final ColumnConverter converter;

    public MaconRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Macon} stored in the database.
     */
    @Override
    public Macon apply(Row row, String prefix) {
        Macon entity = new Macon();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLibelle(converter.fromRow(row, prefix + "_libelle", String.class));
        return entity;
    }
}
