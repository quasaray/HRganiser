package com.quasaray.hrganiser.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.quasaray.hrganiser.domain.Organisation;
import com.quasaray.hrganiser.repository.OrganisationRepository;
import com.quasaray.hrganiser.repository.search.OrganisationSearchRepository;
import com.quasaray.hrganiser.service.OrganisationService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Organisation}.
 */
@Service
@Transactional
public class OrganisationServiceImpl implements OrganisationService {

    private final Logger log = LoggerFactory.getLogger(OrganisationServiceImpl.class);

    private final OrganisationRepository organisationRepository;

    private final OrganisationSearchRepository organisationSearchRepository;

    public OrganisationServiceImpl(
        OrganisationRepository organisationRepository,
        OrganisationSearchRepository organisationSearchRepository
    ) {
        this.organisationRepository = organisationRepository;
        this.organisationSearchRepository = organisationSearchRepository;
    }

    @Override
    public Organisation save(Organisation organisation) {
        log.debug("Request to save Organisation : {}", organisation);
        Organisation result = organisationRepository.save(organisation);
        organisationSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Organisation> partialUpdate(Organisation organisation) {
        log.debug("Request to partially update Organisation : {}", organisation);

        return organisationRepository
            .findById(organisation.getId())
            .map(existingOrganisation -> {
                if (organisation.getOrgName() != null) {
                    existingOrganisation.setOrgName(organisation.getOrgName());
                }

                return existingOrganisation;
            })
            .map(organisationRepository::save)
            .map(savedOrganisation -> {
                organisationSearchRepository.save(savedOrganisation);

                return savedOrganisation;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Organisation> findAll() {
        log.debug("Request to get all Organisations");
        return organisationRepository.findAllWithEagerRelationships();
    }

    public Page<Organisation> findAllWithEagerRelationships(Pageable pageable) {
        return organisationRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Organisation> findOne(Long id) {
        log.debug("Request to get Organisation : {}", id);
        return organisationRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Organisation : {}", id);
        organisationRepository.deleteById(id);
        organisationSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Organisation> search(String query) {
        log.debug("Request to search Organisations for query {}", query);
        return StreamSupport.stream(organisationSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
