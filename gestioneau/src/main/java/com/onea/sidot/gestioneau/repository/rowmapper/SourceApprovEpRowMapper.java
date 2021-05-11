package com.onea.sidot.gestioneau.repository.rowmapper;

import com.onea.sidot.gestioneau.domain.SourceApprovEp;
import com.onea.sidot.gestioneau.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link SourceApprovEp}, with proper type conversions.
 */
@Service
public class SourceApprovEpRowMapper implements BiFunction<Row, String, SourceApprovEp> {

    private final ColumnConverter converter;

    public SourceApprovEpRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link SourceApprovEp} stored in the database.
     */
    @Override
    public SourceApprovEp apply(Row row, String prefix) {
        SourceApprovEp entity = new SourceApprovEp();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLibelle(converter.fromRow(row, prefix + "_libelle", String.class));
        return entity;
    }
}
