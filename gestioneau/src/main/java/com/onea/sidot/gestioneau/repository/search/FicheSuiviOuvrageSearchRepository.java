package com.onea.sidot.gestioneau.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.onea.sidot.gestioneau.domain.FicheSuiviOuvrage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link FicheSuiviOuvrage} entity.
 */
public interface FicheSuiviOuvrageSearchRepository
    extends ReactiveElasticsearchRepository<FicheSuiviOuvrage, Long>, FicheSuiviOuvrageSearchRepositoryInternal {}

interface FicheSuiviOuvrageSearchRepositoryInternal {
    Flux<FicheSuiviOuvrage> search(String query, Pageable pageable);
}

class FicheSuiviOuvrageSearchRepositoryInternalImpl implements FicheSuiviOuvrageSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    FicheSuiviOuvrageSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<FicheSuiviOuvrage> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, FicheSuiviOuvrage.class).map(SearchHit::getContent);
    }
}
