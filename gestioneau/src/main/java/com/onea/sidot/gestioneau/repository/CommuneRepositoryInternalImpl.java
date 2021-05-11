package com.onea.sidot.gestioneau.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.onea.sidot.gestioneau.domain.Commune;
import com.onea.sidot.gestioneau.repository.rowmapper.CommuneRowMapper;
import com.onea.sidot.gestioneau.repository.rowmapper.ProvinceRowMapper;
import com.onea.sidot.gestioneau.repository.rowmapper.TypeCommuneRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Commune entity.
 */
@SuppressWarnings("unused")
class CommuneRepositoryInternalImpl implements CommuneRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProvinceRowMapper provinceMapper;
    private final TypeCommuneRowMapper typecommuneMapper;
    private final CommuneRowMapper communeMapper;

    private static final Table entityTable = Table.aliased("commune", EntityManager.ENTITY_ALIAS);
    private static final Table provinceTable = Table.aliased("province", "province");
    private static final Table typecommuneTable = Table.aliased("type_commune", "typecommune");

    public CommuneRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProvinceRowMapper provinceMapper,
        TypeCommuneRowMapper typecommuneMapper,
        CommuneRowMapper communeMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.provinceMapper = provinceMapper;
        this.typecommuneMapper = typecommuneMapper;
        this.communeMapper = communeMapper;
    }

    @Override
    public Flux<Commune> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Commune> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Commune> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = CommuneSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProvinceSqlHelper.getColumns(provinceTable, "province"));
        columns.addAll(TypeCommuneSqlHelper.getColumns(typecommuneTable, "typecommune"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(provinceTable)
            .on(Column.create("province_id", entityTable))
            .equals(Column.create("id", provinceTable))
            .leftOuterJoin(typecommuneTable)
            .on(Column.create("typecommune_id", entityTable))
            .equals(Column.create("id", typecommuneTable));

        String select = entityManager.createSelect(selectFrom, Commune.class, pageable, criteria);
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
    public Flux<Commune> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Commune> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Commune process(Row row, RowMetadata metadata) {
        Commune entity = communeMapper.apply(row, "e");
        entity.setProvince(provinceMapper.apply(row, "province"));
        entity.setTypecommune(typecommuneMapper.apply(row, "typecommune"));
        return entity;
    }

    @Override
    public <S extends Commune> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Commune> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Commune with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Commune entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class CommuneSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("libelle", table, columnPrefix + "_libelle"));

        columns.add(Column.aliased("province_id", table, columnPrefix + "_province_id"));
        columns.add(Column.aliased("typecommune_id", table, columnPrefix + "_typecommune_id"));
        return columns;
    }
}
