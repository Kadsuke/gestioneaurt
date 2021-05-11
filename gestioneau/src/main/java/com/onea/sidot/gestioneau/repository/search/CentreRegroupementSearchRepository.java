package com.onea.sidot.gestioneau.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.onea.sidot.gestioneau.domain.CentreRegroupement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link CentreRegroupement} entity.
 */
public interface CentreRegroupementSearchRepository
    extends ReactiveElasticsearchRepository<CentreRegroupement, Long>, CentreRegroupementSearchRepositoryInternal {}

interface CentreRegroupementSearchRepositoryInternal {
    Flux<CentreRegroupement> search(String query, Pageable pageable);
}

class CentreRegroupementSearchRepositoryInternalImpl implements CentreRegroupementSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    CentreRegroupementSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<CentreRegroupement> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, CentreRegroupement.class).map(SearchHit::getContent);
    }
}
