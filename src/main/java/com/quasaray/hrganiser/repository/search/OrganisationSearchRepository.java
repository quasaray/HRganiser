package com.quasaray.hrganiser.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.quasaray.hrganiser.domain.Organisation;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Organisation} entity.
 */
public interface OrganisationSearchRepository extends ElasticsearchRepository<Organisation, Long>, OrganisationSearchRepositoryInternal {}

interface OrganisationSearchRepositoryInternal {
    Stream<Organisation> search(String query);
}

class OrganisationSearchRepositoryInternalImpl implements OrganisationSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    OrganisationSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Organisation> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Organisation.class).map(SearchHit::getContent).stream();
    }
}
