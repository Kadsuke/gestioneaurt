package com.onea.sidot.gestioneau.repository.rowmapper;

import com.onea.sidot.gestioneau.domain.NatureOuvrage;
import com.onea.sidot.gestioneau.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link NatureOuvrage}, with proper type conversions.
 */
@Service
public class NatureOuvrageRowMapper implements BiFunction<Row, String, NatureOuvrage> {

    private final ColumnConverter converter;

    public NatureOuvrageRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link NatureOuvrage} stored in the database.
     */
    @Override
    public NatureOuvrage apply(Row row, String prefix) {
        NatureOuvrage entity = new NatureOuvrage();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLibelle(converter.fromRow(row, prefix + "_libelle", String.class));
        return entity;
    }
}
