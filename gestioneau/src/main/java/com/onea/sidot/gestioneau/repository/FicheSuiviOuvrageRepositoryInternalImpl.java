package com.onea.sidot.gestioneau.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.onea.sidot.gestioneau.domain.FicheSuiviOuvrage;
import com.onea.sidot.gestioneau.repository.rowmapper.FicheSuiviOuvrageRowMapper;
import com.onea.sidot.gestioneau.repository.rowmapper.MaconRowMapper;
import com.onea.sidot.gestioneau.repository.rowmapper.ModeEvacExcretaRowMapper;
import com.onea.sidot.gestioneau.repository.rowmapper.ModeEvacuationEauUseeRowMapper;
import com.onea.sidot.gestioneau.repository.rowmapper.NatureOuvrageRowMapper;
import com.onea.sidot.gestioneau.repository.rowmapper.ParcelleRowMapper;
import com.onea.sidot.gestioneau.repository.rowmapper.PrefabricantRowMapper;
import com.onea.sidot.gestioneau.repository.rowmapper.PrevisionRowMapper;
import com.onea.sidot.gestioneau.repository.rowmapper.SourceApprovEpRowMapper;
import com.onea.sidot.gestioneau.repository.rowmapper.TypeHabitationRowMapper;
import com.onea.sidot.gestioneau.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
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
 * Spring Data SQL reactive custom repository implementation for the FicheSuiviOuvrage entity.
 */
@SuppressWarnings("unused")
class FicheSuiviOuvrageRepositoryInternalImpl implements FicheSuiviOuvrageRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ParcelleRowMapper parcelleMapper;
    private final PrevisionRowMapper previsionMapper;
    private final NatureOuvrageRowMapper natureouvrageMapper;
    private final TypeHabitationRowMapper typehabitationMapper;
    private final SourceApprovEpRowMapper sourceapprovepMapper;
    private final ModeEvacuationEauUseeRowMapper modeevacuationeauuseeMapper;
    private final ModeEvacExcretaRowMapper modeevacexcretaMapper;
    private final MaconRowMapper maconMapper;
    private final PrefabricantRowMapper prefabricantMapper;
    private final FicheSuiviOuvrageRowMapper fichesuiviouvrageMapper;

    private static final Table entityTable = Table.aliased("fiche_suivi_ouvrage", EntityManager.ENTITY_ALIAS);
    private static final Table parcelleTable = Table.aliased("parcelle", "parcelle");
    private static final Table previsionTable = Table.aliased("prevision", "prevision");
    private static final Table natureouvrageTable = Table.aliased("nature_ouvrage", "natureouvrage");
    private static final Table typehabitationTable = Table.aliased("type_habitation", "typehabitation");
    private static final Table sourceapprovepTable = Table.aliased("source_approv_ep", "sourceapprovep");
    private static final Table modeevacuationeauuseeTable = Table.aliased("mode_evacuation_eau_usee", "modeevacuationeauusee");
    private static final Table modeevacexcretaTable = Table.aliased("mode_evac_excreta", "modeevacexcreta");
    private static final Table maconTable = Table.aliased("macon", "macon");
    private static final Table prefabricantTable = Table.aliased("prefabricant", "prefabricant");

    public FicheSuiviOuvrageRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ParcelleRowMapper parcelleMapper,
        PrevisionRowMapper previsionMapper,
        NatureOuvrageRowMapper natureouvrageMapper,
        TypeHabitationRowMapper typehabitationMapper,
        SourceApprovEpRowMapper sourceapprovepMapper,
        ModeEvacuationEauUseeRowMapper modeevacuationeauuseeMapper,
        ModeEvacExcretaRowMapper modeevacexcretaMapper,
        MaconRowMapper maconMapper,
        PrefabricantRowMapper prefabricantMapper,
        FicheSuiviOuvrageRowMapper fichesuiviouvrageMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.parcelleMapper = parcelleMapper;
        this.previsionMapper = previsionMapper;
        this.natureouvrageMapper = natureouvrageMapper;
        this.typehabitationMapper = typehabitationMapper;
        this.sourceapprovepMapper = sourceapprovepMapper;
        this.modeevacuationeauuseeMapper = modeevacuationeauuseeMapper;
        this.modeevacexcretaMapper = modeevacexcretaMapper;
        this.maconMapper = maconMapper;
        this.prefabricantMapper = prefabricantMapper;
        this.fichesuiviouvrageMapper = fichesuiviouvrageMapper;
    }

    @Override
    public Flux<FicheSuiviOuvrage> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<FicheSuiviOuvrage> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<FicheSuiviOuvrage> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = FicheSuiviOuvrageSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ParcelleSqlHelper.getColumns(parcelleTable, "parcelle"));
        columns.addAll(PrevisionSqlHelper.getColumns(previsionTable, "prevision"));
        columns.addAll(NatureOuvrageSqlHelper.getColumns(natureouvrageTable, "natureouvrage"));
        columns.addAll(TypeHabitationSqlHelper.getColumns(typehabitationTable, "typehabitation"));
        columns.addAll(SourceApprovEpSqlHelper.getColumns(sourceapprovepTable, "sourceapprovep"));
        columns.addAll(ModeEvacuationEauUseeSqlHelper.getColumns(modeevacuationeauuseeTable, "modeevacuationeauusee"));
        columns.addAll(ModeEvacExcretaSqlHelper.getColumns(modeevacexcretaTable, "modeevacexcreta"));
        columns.addAll(MaconSqlHelper.getColumns(maconTable, "macon"));
        columns.addAll(PrefabricantSqlHelper.getColumns(prefabricantTable, "prefabricant"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(parcelleTable)
            .on(Column.create("parcelle_id", entityTable))
            .equals(Column.create("id", parcelleTable))
            .leftOuterJoin(previsionTable)
            .on(Column.create("prevision_id", entityTable))
            .equals(Column.create("id", previsionTable))
            .leftOuterJoin(natureouvrageTable)
            .on(Column.create("natureouvrage_id", entityTable))
            .equals(Column.create("id", natureouvrageTable))
            .leftOuterJoin(typehabitationTable)
            .on(Column.create("typehabitation_id", entityTable))
            .equals(Column.create("id", typehabitationTable))
            .leftOuterJoin(sourceapprovepTable)
            .on(Column.create("sourceapprovep_id", entityTable))
            .equals(Column.create("id", sourceapprovepTable))
            .leftOuterJoin(modeevacuationeauuseeTable)
            .on(Column.create("modeevacuationeauusee_id", entityTable))
            .equals(Column.create("id", modeevacuationeauuseeTable))
            .leftOuterJoin(modeevacexcretaTable)
            .on(Column.create("modeevacexcreta_id", entityTable))
            .equals(Column.create("id", modeevacexcretaTable))
            .leftOuterJoin(maconTable)
            .on(Column.create("macon_id", entityTable))
            .equals(Column.create("id", maconTable))
            .leftOuterJoin(prefabricantTable)
            .on(Column.create("prefabricant_id", entityTable))
            .equals(Column.create("id", prefabricantTable));

        String select = entityManager.createSelect(selectFrom, FicheSuiviOuvrage.class, pageable, criteria);
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
    public Flux<FicheSuiviOuvrage> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<FicheSuiviOuvrage> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private FicheSuiviOuvrage process(Row row, RowMetadata metadata) {
        FicheSuiviOuvrage entity = fichesuiviouvrageMapper.apply(row, "e");
        entity.setParcelle(parcelleMapper.apply(row, "parcelle"));
        entity.setPrevision(previsionMapper.apply(row, "prevision"));
        entity.setNatureouvrage(natureouvrageMapper.apply(row, "natureouvrage"));
        entity.setTypehabitation(typehabitationMapper.apply(row, "typehabitation"));
        entity.setSourceapprovep(sourceapprovepMapper.apply(row, "sourceapprovep"));
        entity.setModeevacuationeauusee(modeevacuationeauuseeMapper.apply(row, "modeevacuationeauusee"));
        entity.setModeevacexcreta(modeevacexcretaMapper.apply(row, "modeevacexcreta"));
        entity.setMacon(maconMapper.apply(row, "macon"));
        entity.setPrefabricant(prefabricantMapper.apply(row, "prefabricant"));
        return entity;
    }

    @Override
    public <S extends FicheSuiviOuvrage> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends FicheSuiviOuvrage> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update FicheSuiviOuvrage with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(FicheSuiviOuvrage entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class FicheSuiviOuvrageSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("prj_appuis", table, columnPrefix + "_prj_appuis"));
        columns.add(Column.aliased("nom_benef", table, columnPrefix + "_nom_benef"));
        columns.add(Column.aliased("prenom_benef", table, columnPrefix + "_prenom_benef"));
        columns.add(Column.aliased("profession_benef", table, columnPrefix + "_profession_benef"));
        columns.add(Column.aliased("nb_usagers", table, columnPrefix + "_nb_usagers"));
        columns.add(Column.aliased("contacts", table, columnPrefix + "_contacts"));
        columns.add(Column.aliased("longitude", table, columnPrefix + "_longitude"));
        columns.add(Column.aliased("latitude", table, columnPrefix + "_latitude"));
        columns.add(Column.aliased("date_remise_devis", table, columnPrefix + "_date_remise_devis"));
        columns.add(Column.aliased("date_debut_travaux", table, columnPrefix + "_date_debut_travaux"));
        columns.add(Column.aliased("date_fin_travaux", table, columnPrefix + "_date_fin_travaux"));
        columns.add(Column.aliased("rue", table, columnPrefix + "_rue"));
        columns.add(Column.aliased("porte", table, columnPrefix + "_porte"));
        columns.add(Column.aliased("cout_menage", table, columnPrefix + "_cout_menage"));
        columns.add(Column.aliased("subv_onea", table, columnPrefix + "_subv_onea"));
        columns.add(Column.aliased("subv_projet", table, columnPrefix + "_subv_projet"));
        columns.add(Column.aliased("autre_subv", table, columnPrefix + "_autre_subv"));
        columns.add(Column.aliased("toles", table, columnPrefix + "_toles"));
        columns.add(Column.aliased("animateur", table, columnPrefix + "_animateur"));
        columns.add(Column.aliased("superviseur", table, columnPrefix + "_superviseur"));
        columns.add(Column.aliased("controleur", table, columnPrefix + "_controleur"));

        columns.add(Column.aliased("parcelle_id", table, columnPrefix + "_parcelle_id"));
        columns.add(Column.aliased("prevision_id", table, columnPrefix + "_prevision_id"));
        columns.add(Column.aliased("natureouvrage_id", table, columnPrefix + "_natureouvrage_id"));
        columns.add(Column.aliased("typehabitation_id", table, columnPrefix + "_typehabitation_id"));
        columns.add(Column.aliased("sourceapprovep_id", table, columnPrefix + "_sourceapprovep_id"));
        columns.add(Column.aliased("modeevacuationeauusee_id", table, columnPrefix + "_modeevacuationeauusee_id"));
        columns.add(Column.aliased("modeevacexcreta_id", table, columnPrefix + "_modeevacexcreta_id"));
        columns.add(Column.aliased("macon_id", table, columnPrefix + "_macon_id"));
        columns.add(Column.aliased("prefabricant_id", table, columnPrefix + "_prefabricant_id"));
        return columns;
    }
}
