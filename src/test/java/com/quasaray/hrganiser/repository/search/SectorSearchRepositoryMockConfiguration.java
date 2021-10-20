package com.quasaray.hrganiser.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link SectorSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class SectorSearchRepositoryMockConfiguration {

    @MockBean
    private SectorSearchRepository mockSectorSearchRepository;
}
