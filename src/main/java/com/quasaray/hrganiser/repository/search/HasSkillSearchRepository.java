package com.quasaray.hrganiser.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.quasaray.hrganiser.domain.HasSkill;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link HasSkill} entity.
 */
public interface HasSkillSearchRepository extends ElasticsearchRepository<HasSkill, Long>, HasSkillSearchRepositoryInternal {}

interface HasSkillSearchRepositoryInternal {
    Stream<HasSkill> search(String query);
}

class HasSkillSearchRepositoryInternalImpl implements HasSkillSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    HasSkillSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<HasSkill> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, HasSkill.class).map(SearchHit::getContent).stream();
    }
}
