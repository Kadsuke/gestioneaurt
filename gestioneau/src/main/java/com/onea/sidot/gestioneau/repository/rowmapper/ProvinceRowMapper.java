package com.onea.sidot.gestioneau.repository.rowmapper;

import com.onea.sidot.gestioneau.domain.Province;
import com.onea.sidot.gestioneau.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Province}, with proper type conversions.
 */
@Service
public class ProvinceRowMapper implements BiFunction<Row, String, Province> {

    private final ColumnConverter converter;

    public ProvinceRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Province} stored in the database.
     */
    @Override
    public Province apply(Row row, String prefix) {
        Province entity = new Province();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLibelle(converter.fromRow(row, prefix + "_libelle", String.class));
        entity.setRegionId(converter.fromRow(row, prefix + "_region_id", Long.class));
        return entity;
    }
}
