package com.quasaray.hrganiser.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.quasaray.hrganiser.domain.Sector;
import com.quasaray.hrganiser.repository.SectorRepository;
import com.quasaray.hrganiser.repository.search.SectorSearchRepository;
import com.quasaray.hrganiser.service.SectorService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Sector}.
 */
@Service
@Transactional
public class SectorServiceImpl implements SectorService {

    private final Logger log = LoggerFactory.getLogger(SectorServiceImpl.class);

    private final SectorRepository sectorRepository;

    private final SectorSearchRepository sectorSearchRepository;

    public SectorServiceImpl(SectorRepository sectorRepository, SectorSearchRepository sectorSearchRepository) {
        this.sectorRepository = sectorRepository;
        this.sectorSearchRepository = sectorSearchRepository;
    }

    @Override
    public Sector save(Sector sector) {
        log.debug("Request to save Sector : {}", sector);
        Sector result = sectorRepository.save(sector);
        sectorSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Sector> partialUpdate(Sector sector) {
        log.debug("Request to partially update Sector : {}", sector);

        return sectorRepository
            .findById(sector.getId())
            .map(existingSector -> {
                if (sector.getSectorName() != null) {
                    existingSector.setSectorName(sector.getSectorName());
                }

                return existingSector;
            })
            .map(sectorRepository::save)
            .map(savedSector -> {
                sectorSearchRepository.save(savedSector);

                return savedSector;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sector> findAll() {
        log.debug("Request to get all Sectors");
        return sectorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Sector> findOne(Long id) {
        log.debug("Request to get Sector : {}", id);
        return sectorRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Sector : {}", id);
        sectorRepository.deleteById(id);
        sectorSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sector> search(String query) {
        log.debug("Request to search Sectors for query {}", query);
        return StreamSupport.stream(sectorSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
