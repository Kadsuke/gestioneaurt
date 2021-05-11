package com.onea.sidot.gestioneau.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.onea.sidot.gestioneau.domain.Prevision;
import com.onea.sidot.gestioneau.repository.rowmapper.AnneeRowMapper;
import com.onea.sidot.gestioneau.repository.rowmapper.CentreRowMapper;
import com.onea.sidot.gestioneau.repository.rowmapper.PrevisionRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Prevision entity.
 */
@SuppressWarnings("unused")
class PrevisionRepositoryInternalImpl implements PrevisionRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CentreRowMapper centreMapper;
    private final AnneeRowMapper anneeMapper;
    private final PrevisionRowMapper previsionMapper;

    private static final Table entityTable = Table.aliased("prevision", EntityManager.ENTITY_ALIAS);
    private static final Table centreTable = Table.aliased("centre", "centre");
    private static final Table refanneeTable = Table.aliased("annee", "refannee");

    public PrevisionRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CentreRowMapper centreMapper,
        AnneeRowMapper anneeMapper,
        PrevisionRowMapper previsionMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.centreMapper = centreMapper;
        this.anneeMapper = anneeMapper;
        this.previsionMapper = previsionMapper;
    }

    @Override
    public Flux<Prevision> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Prevision> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Prevision> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = PrevisionSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CentreSqlHelper.getColumns(centreTable, "centre"));
        columns.addAll(AnneeSqlHelper.getColumns(refanneeTable, "refannee"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(centreTable)
            .on(Column.create("centre_id", entityTable))
            .equals(Column.create("id", centreTable))
            .leftOuterJoin(refanneeTable)
            .on(Column.create("refannee_id", entityTable))
            .equals(Column.create("id", refanneeTable));

        String select = entityManager.createSelect(selectFrom, Prevision.class, pageable, criteria);
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
    public Flux<Prevision> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Prevision> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Prevision process(Row row, RowMetadata metadata) {
        Prevision entity = previsionMapper.apply(row, "e");
        entity.setCentre(centreMapper.apply(row, "centre"));
        entity.setRefannee(anneeMapper.apply(row, "refannee"));
        return entity;
    }

    @Override
    public <S extends Prevision> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Prevision> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Prevision with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Prevision entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class PrevisionSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("nb_latrine", table, columnPrefix + "_nb_latrine"));
        columns.add(Column.aliased("nb_puisard", table, columnPrefix + "_nb_puisard"));
        columns.add(Column.aliased("nb_public", table, columnPrefix + "_nb_public"));
        columns.add(Column.aliased("nb_scolaire", table, columnPrefix + "_nb_scolaire"));

        columns.add(Column.aliased("centre_id", table, columnPrefix + "_centre_id"));
        columns.add(Column.aliased("refannee_id", table, columnPrefix + "_refannee_id"));
        return columns;
    }
}
