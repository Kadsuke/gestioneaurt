package com.onea.sidot.gestioneau.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.onea.sidot.gestioneau.domain.Province;
import com.onea.sidot.gestioneau.repository.rowmapper.ProvinceRowMapper;
import com.onea.sidot.gestioneau.repository.rowmapper.RegionRowMapper;
import com.onea.sidot.gestioneau.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Province entity.
 */
@SuppressWarnings("unused")
class ProvinceRepositoryInternalImpl implements ProvinceRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RegionRowMapper regionMapper;
    private final ProvinceRowMapper provinceMapper;

    private static final Table entityTable = Table.aliased("province", EntityManager.ENTITY_ALIAS);
    private static final Table regionTable = Table.aliased("region", "region");

    public ProvinceRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RegionRowMapper regionMapper,
        ProvinceRowMapper provinceMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.regionMapper = regionMapper;
        this.provinceMapper = provinceMapper;
    }

    @Override
    public Flux<Province> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Province> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Province> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = ProvinceSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(RegionSqlHelper.getColumns(regionTable, "region"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(regionTable)
            .on(Column.create("region_id", entityTable))
            .equals(Column.create("id", regionTable));

        String select = entityManager.createSelect(selectFrom, Province.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<Province> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Province> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Province process(Row row, RowMetadata metadata) {
        Province entity = provinceMapper.apply(row, "e");
        entity.setRegion(regionMapper.apply(row, "region"));
        return entity;
    }

    @Override
    public <S extends Province> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Province> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Province with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Province entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class ProvinceSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("libelle", table, columnPrefix + "_libelle"));

        columns.add(Column.aliased("region_id", table, columnPrefix + "_region_id"));
        return columns;
    }
}
