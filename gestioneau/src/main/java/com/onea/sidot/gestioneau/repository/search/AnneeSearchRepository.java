package com.onea.sidot.gestioneau.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.onea.sidot.gestioneau.domain.Annee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Annee} entity.
 */
public interface AnneeSearchRepository extends ReactiveElasticsearchRepository<Annee, Long>, AnneeSearchRepositoryInternal {}

interface AnneeSearchRepositoryInternal {
    Flux<Annee> search(String query, Pageable pageable);
}

class AnneeSearchRepositoryInternalImpl implements AnneeSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    AnneeSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Annee> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, Annee.class).map(SearchHit::getContent);
    }
}
