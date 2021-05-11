package com.onea.sidot.gestioneau.repository.rowmapper;

import com.onea.sidot.gestioneau.domain.Prefabricant;
import com.onea.sidot.gestioneau.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Prefabricant}, with proper type conversions.
 */
@Service
public class PrefabricantRowMapper implements BiFunction<Row, String, Prefabricant> {

    private final ColumnConverter converter;

    public PrefabricantRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Prefabricant} stored in the database.
     */
    @Override
    public Prefabricant apply(Row row, String prefix) {
        Prefabricant entity = new Prefabricant();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLibelle(converter.fromRow(row, prefix + "_libelle", String.class));
        return entity;
    }
}
