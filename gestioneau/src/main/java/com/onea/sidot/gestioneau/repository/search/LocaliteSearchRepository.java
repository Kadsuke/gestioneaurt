package com.onea.sidot.gestioneau.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.onea.sidot.gestioneau.domain.Localite;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Localite} entity.
 */
public interface LocaliteSearchRepository extends ReactiveElasticsearchRepository<Localite, Long>, LocaliteSearchRepositoryInternal {}

interface LocaliteSearchRepositoryInternal {
    Flux<Localite> search(String query, Pageable pageable);
}

class LocaliteSearchRepositoryInternalImpl implements LocaliteSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    LocaliteSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Localite> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, Localite.class).map(SearchHit::getContent);
    }
}
