package com.onea.sidot.gestioneau.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.onea.sidot.gestioneau.domain.ModeEvacExcreta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link ModeEvacExcreta} entity.
 */
public interface ModeEvacExcretaSearchRepository
    extends ReactiveElasticsearchRepository<ModeEvacExcreta, Long>, ModeEvacExcretaSearchRepositoryInternal {}

interface ModeEvacExcretaSearchRepositoryInternal {
    Flux<ModeEvacExcreta> search(String query, Pageable pageable);
}

class ModeEvacExcretaSearchRepositoryInternalImpl implements ModeEvacExcretaSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    ModeEvacExcretaSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<ModeEvacExcreta> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, ModeEvacExcreta.class).map(SearchHit::getContent);
    }
}
