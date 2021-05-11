package com.onea.sidot.gestioneau.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.onea.sidot.gestioneau.domain.SourceApprovEp;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link SourceApprovEp} entity.
 */
public interface SourceApprovEpSearchRepository
    extends ReactiveElasticsearchRepository<SourceApprovEp, Long>, SourceApprovEpSearchRepositoryInternal {}

interface SourceApprovEpSearchRepositoryInternal {
    Flux<SourceApprovEp> search(String query, Pageable pageable);
}

class SourceApprovEpSearchRepositoryInternalImpl implements SourceApprovEpSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    SourceApprovEpSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<SourceApprovEp> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, SourceApprovEp.class).map(SearchHit::getContent);
    }
}
