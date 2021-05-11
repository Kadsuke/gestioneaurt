package com.onea.sidot.gestioneau.repository.rowmapper;

import com.onea.sidot.gestioneau.domain.TypeCommune;
import com.onea.sidot.gestioneau.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TypeCommune}, with proper type conversions.
 */
@Service
public class TypeCommuneRowMapper implements BiFunction<Row, String, TypeCommune> {

    private final ColumnConverter converter;

    public TypeCommuneRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TypeCommune} stored in the database.
     */
    @Override
    public TypeCommune apply(Row row, String prefix) {
        TypeCommune entity = new TypeCommune();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLibelle(converter.fromRow(row, prefix + "_libelle", String.class));
        return entity;
    }
}
