package com.onea.sidot.gestioneau.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.onea.sidot.gestioneau.domain.Prefabricant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Prefabricant} entity.
 */
public interface PrefabricantSearchRepository
    extends ReactiveElasticsearchRepository<Prefabricant, Long>, PrefabricantSearchRepositoryInternal {}

interface PrefabricantSearchRepositoryInternal {
    Flux<Prefabricant> search(String query, Pageable pageable);
}

class PrefabricantSearchRepositoryInternalImpl implements PrefabricantSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    PrefabricantSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Prefabricant> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, Prefabricant.class).map(SearchHit::getContent);
    }
}
