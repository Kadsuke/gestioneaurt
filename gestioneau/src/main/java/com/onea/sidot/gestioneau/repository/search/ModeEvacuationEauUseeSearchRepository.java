package com.onea.sidot.gestioneau.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.onea.sidot.gestioneau.domain.ModeEvacuationEauUsee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link ModeEvacuationEauUsee} entity.
 */
public interface ModeEvacuationEauUseeSearchRepository
    extends ReactiveElasticsearchRepository<ModeEvacuationEauUsee, Long>, ModeEvacuationEauUseeSearchRepositoryInternal {}

interface ModeEvacuationEauUseeSearchRepositoryInternal {
    Flux<ModeEvacuationEauUsee> search(String query, Pageable pageable);
}

class ModeEvacuationEauUseeSearchRepositoryInternalImpl implements ModeEvacuationEauUseeSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    ModeEvacuationEauUseeSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<ModeEvacuationEauUsee> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, ModeEvacuationEauUsee.class).map(SearchHit::getContent);
    }
}
