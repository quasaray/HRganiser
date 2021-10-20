package com.quasaray.hrganiser.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link HasSkillSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class HasSkillSearchRepositoryMockConfiguration {

    @MockBean
    private HasSkillSearchRepository mockHasSkillSearchRepository;
}
