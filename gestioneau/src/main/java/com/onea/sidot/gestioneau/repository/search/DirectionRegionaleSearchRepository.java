package com.onea.sidot.gestioneau.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.onea.sidot.gestioneau.domain.DirectionRegionale;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link DirectionRegionale} entity.
 */
public interface DirectionRegionaleSearchRepository
    extends ReactiveElasticsearchRepository<DirectionRegionale, Long>, DirectionRegionaleSearchRepositoryInternal {}

interface DirectionRegionaleSearchRepositoryInternal {
    Flux<DirectionRegionale> search(String query, Pageable pageable);
}

class DirectionRegionaleSearchRepositoryInternalImpl implements DirectionRegionaleSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    DirectionRegionaleSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<DirectionRegionale> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, DirectionRegionale.class).map(SearchHit::getContent);
    }
}
