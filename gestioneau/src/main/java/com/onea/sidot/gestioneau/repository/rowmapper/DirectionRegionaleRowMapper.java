package com.onea.sidot.gestioneau.repository.rowmapper;

import com.onea.sidot.gestioneau.domain.DirectionRegionale;
import com.onea.sidot.gestioneau.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link DirectionRegionale}, with proper type conversions.
 */
@Service
public class DirectionRegionaleRowMapper implements BiFunction<Row, String, DirectionRegionale> {

    private final ColumnConverter converter;

    public DirectionRegionaleRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link DirectionRegionale} stored in the database.
     */
    @Override
    public DirectionRegionale apply(Row row, String prefix) {
        DirectionRegionale entity = new DirectionRegionale();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLibelle(converter.fromRow(row, prefix + "_libelle", String.class));
        entity.setResponsable(converter.fromRow(row, prefix + "_responsable", String.class));
        entity.setContact(converter.fromRow(row, prefix + "_contact", String.class));
        return entity;
    }
}
