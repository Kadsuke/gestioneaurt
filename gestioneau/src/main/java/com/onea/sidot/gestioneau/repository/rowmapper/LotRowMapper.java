package com.onea.sidot.gestioneau.repository.rowmapper;

import com.onea.sidot.gestioneau.domain.Lot;
import com.onea.sidot.gestioneau.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Lot}, with proper type conversions.
 */
@Service
public class LotRowMapper implements BiFunction<Row, String, Lot> {

    private final ColumnConverter converter;

    public LotRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Lot} stored in the database.
     */
    @Override
    public Lot apply(Row row, String prefix) {
        Lot entity = new Lot();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLibelle(converter.fromRow(row, prefix + "_libelle", String.class));
        entity.setSectionId(converter.fromRow(row, prefix + "_section_id", Long.class));
        return entity;
    }
}
