package com.onea.sidot.gestioneau.repository.rowmapper;

import com.onea.sidot.gestioneau.domain.ModeEvacExcreta;
import com.onea.sidot.gestioneau.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ModeEvacExcreta}, with proper type conversions.
 */
@Service
public class ModeEvacExcretaRowMapper implements BiFunction<Row, String, ModeEvacExcreta> {

    private final ColumnConverter converter;

    public ModeEvacExcretaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ModeEvacExcreta} stored in the database.
     */
    @Override
    public ModeEvacExcreta apply(Row row, String prefix) {
        ModeEvacExcreta entity = new ModeEvacExcreta();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLibelle(converter.fromRow(row, prefix + "_libelle", String.class));
        return entity;
    }
}
