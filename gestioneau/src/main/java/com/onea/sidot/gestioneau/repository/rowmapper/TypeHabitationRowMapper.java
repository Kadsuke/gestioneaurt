package com.onea.sidot.gestioneau.repository.rowmapper;

import com.onea.sidot.gestioneau.domain.TypeHabitation;
import com.onea.sidot.gestioneau.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TypeHabitation}, with proper type conversions.
 */
@Service
public class TypeHabitationRowMapper implements BiFunction<Row, String, TypeHabitation> {

    private final ColumnConverter converter;

    public TypeHabitationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TypeHabitation} stored in the database.
     */
    @Override
    public TypeHabitation apply(Row row, String prefix) {
        TypeHabitation entity = new TypeHabitation();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLibelle(converter.fromRow(row, prefix + "_libelle", String.class));
        return entity;
    }
}
