package com.onea.sidot.gestioneau.repository.rowmapper;

import com.onea.sidot.gestioneau.domain.ModeEvacuationEauUsee;
import com.onea.sidot.gestioneau.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ModeEvacuationEauUsee}, with proper type conversions.
 */
@Service
public class ModeEvacuationEauUseeRowMapper implements BiFunction<Row, String, ModeEvacuationEauUsee> {

    private final ColumnConverter converter;

    public ModeEvacuationEauUseeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ModeEvacuationEauUsee} stored in the database.
     */
    @Override
    public ModeEvacuationEauUsee apply(Row row, String prefix) {
        ModeEvacuationEauUsee entity = new ModeEvacuationEauUsee();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLibelle(converter.fromRow(row, prefix + "_libelle", String.class));
        return entity;
    }
}
