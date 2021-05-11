package com.onea.sidot.gestioneau.repository.rowmapper;

import com.onea.sidot.gestioneau.domain.Section;
import com.onea.sidot.gestioneau.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Section}, with proper type conversions.
 */
@Service
public class SectionRowMapper implements BiFunction<Row, String, Section> {

    private final ColumnConverter converter;

    public SectionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Section} stored in the database.
     */
    @Override
    public Section apply(Row row, String prefix) {
        Section entity = new Section();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLibelle(converter.fromRow(row, prefix + "_libelle", String.class));
        entity.setSecteurId(converter.fromRow(row, prefix + "_secteur_id", Long.class));
        return entity;
    }
}
