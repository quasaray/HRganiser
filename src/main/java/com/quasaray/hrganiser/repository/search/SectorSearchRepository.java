package com.quasaray.hrganiser.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.quasaray.hrganiser.domain.Sector;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Sector} entity.
 */
public interface SectorSearchRepository extends ElasticsearchRepository<Sector, Long>, SectorSearchRepositoryInternal {}

interface SectorSearchRepositoryInternal {
    Stream<Sector> search(String query);
}

class SectorSearchRepositoryInternalImpl implements SectorSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    SectorSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<Sector> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, Sector.class).map(SearchHit::getContent).stream();
    }
}
