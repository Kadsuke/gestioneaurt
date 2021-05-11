package com.onea.sidot.gestioneau.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.onea.sidot.gestioneau.domain.Lot;
import com.onea.sidot.gestioneau.repository.rowmapper.LotRowMapper;
import com.onea.sidot.gestioneau.repository.rowmapper.SectionRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Lot entity.
 */
@SuppressWarnings("unused")
class LotRepositoryInternalImpl implements LotRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final SectionRowMapper sectionMapper;
    private final LotRowMapper lotMapper;

    private static final Table entityTable = Table.aliased("lot", EntityManager.ENTITY_ALIAS);
    private static final Table sectionTable = Table.aliased("section", "section");

    public LotRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        SectionRowMapper sectionMapper,
        LotRowMapper lotMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.sectionMapper = sectionMapper;
        this.lotMapper = lotMapper;
    }

    @Override
    public Flux<Lot> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Lot> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Lot> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = LotSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(SectionSqlHelper.getColumns(sectionTable, "section"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(sectionTable)
            .on(Column.create("section_id", entityTable))
            .equals(Column.create("id", sectionTable));

        String select = entityManager.createSelect(selectFrom, Lot.class, pageable, criteria);
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
    public Flux<Lot> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Lot> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Lot process(Row row, RowMetadata metadata) {
        Lot entity = lotMapper.apply(row, "e");
        entity.setSection(sectionMapper.apply(row, "section"));
        return entity;
    }

    @Override
    public <S extends Lot> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Lot> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Lot with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Lot entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class LotSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("libelle", table, columnPrefix + "_libelle"));

        columns.add(Column.aliased("section_id", table, columnPrefix + "_section_id"));
        return columns;
    }
}
