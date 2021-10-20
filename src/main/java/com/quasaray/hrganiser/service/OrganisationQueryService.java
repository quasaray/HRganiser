package com.quasaray.hrganiser.service;

import com.quasaray.hrganiser.domain.*; // for static metamodels
import com.quasaray.hrganiser.domain.Organisation;
import com.quasaray.hrganiser.repository.OrganisationRepository;
import com.quasaray.hrganiser.repository.search.OrganisationSearchRepository;
import com.quasaray.hrganiser.service.criteria.OrganisationCriteria;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Organisation} entities in the database.
 * The main input is a {@link OrganisationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Organisation} or a {@link Page} of {@link Organisation} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrganisationQueryService extends QueryService<Organisation> {

    private final Logger log = LoggerFactory.getLogger(OrganisationQueryService.class);

    private final OrganisationRepository organisationRepository;

    private final OrganisationSearchRepository organisationSearchRepository;

    public OrganisationQueryService(
        OrganisationRepository organisationRepository,
        OrganisationSearchRepository organisationSearchRepository
    ) {
        this.organisationRepository = organisationRepository;
        this.organisationSearchRepository = organisationSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Organisation} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Organisation> findByCriteria(OrganisationCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Organisation> specification = createSpecification(criteria);
        return organisationRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Organisation} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Organisation> findByCriteria(OrganisationCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Organisation> specification = createSpecification(criteria);
        return organisationRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrganisationCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Organisation> specification = createSpecification(criteria);
        return organisationRepository.count(specification);
    }

    /**
     * Function to convert {@link OrganisationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Organisation> createSpecification(OrganisationCriteria criteria) {
        Specification<Organisation> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Organisation_.id));
            }
            if (criteria.getOrgName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOrgName(), Organisation_.orgName));
            }
            if (criteria.getStaffId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getStaffId(), root -> root.join(Organisation_.staff, JoinType.LEFT).get(User_.id))
                    );
            }
            if (criteria.getEmployeesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getEmployeesId(),
                            root -> root.join(Organisation_.employees, JoinType.LEFT).get(Employee_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
