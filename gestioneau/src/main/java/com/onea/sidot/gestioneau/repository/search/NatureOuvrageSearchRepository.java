package com.onea.sidot.gestioneau.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.onea.sidot.gestioneau.domain.NatureOuvrage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link NatureOuvrage} entity.
 */
public interface NatureOuvrageSearchRepository
    extends ReactiveElasticsearchRepository<NatureOuvrage, Long>, NatureOuvrageSearchRepositoryInternal {}

interface NatureOuvrageSearchRepositoryInternal {
    Flux<NatureOuvrage> search(String query, Pageable pageable);
}

class NatureOuvrageSearchRepositoryInternalImpl implements NatureOuvrageSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    NatureOuvrageSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<NatureOuvrage> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, NatureOuvrage.class).map(SearchHit::getContent);
    }
}
