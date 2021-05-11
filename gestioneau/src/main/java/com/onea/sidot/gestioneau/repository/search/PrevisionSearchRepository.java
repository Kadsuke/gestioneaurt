package com.onea.sidot.gestioneau.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.onea.sidot.gestioneau.domain.Prevision;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Prevision} entity.
 */
public interface PrevisionSearchRepository extends ReactiveElasticsearchRepository<Prevision, Long>, PrevisionSearchRepositoryInternal {}

interface PrevisionSearchRepositoryInternal {
    Flux<Prevision> search(String query, Pageable pageable);
}

class PrevisionSearchRepositoryInternalImpl implements PrevisionSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    PrevisionSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Prevision> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, Prevision.class).map(SearchHit::getContent);
    }
}
