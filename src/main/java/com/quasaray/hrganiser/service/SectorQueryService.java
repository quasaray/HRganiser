package com.quasaray.hrganiser.service;

import com.quasaray.hrganiser.domain.*; // for static metamodels
import com.quasaray.hrganiser.domain.Sector;
import com.quasaray.hrganiser.repository.SectorRepository;
import com.quasaray.hrganiser.repository.search.SectorSearchRepository;
import com.quasaray.hrganiser.service.criteria.SectorCriteria;
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
 * Service for executing complex queries for {@link Sector} entities in the database.
 * The main input is a {@link SectorCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Sector} or a {@link Page} of {@link Sector} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SectorQueryService extends QueryService<Sector> {

    private final Logger log = LoggerFactory.getLogger(SectorQueryService.class);

    private final SectorRepository sectorRepository;

    private final SectorSearchRepository sectorSearchRepository;

    public SectorQueryService(SectorRepository sectorRepository, SectorSearchRepository sectorSearchRepository) {
        this.sectorRepository = sectorRepository;
        this.sectorSearchRepository = sectorSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Sector} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Sector> findByCriteria(SectorCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Sector> specification = createSpecification(criteria);
        return sectorRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Sector} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Sector> findByCriteria(SectorCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Sector> specification = createSpecification(criteria);
        return sectorRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SectorCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Sector> specification = createSpecification(criteria);
        return sectorRepository.count(specification);
    }

    /**
     * Function to convert {@link SectorCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Sector> createSpecification(SectorCriteria criteria) {
        Specification<Sector> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Sector_.id));
            }
            if (criteria.getSectorName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSectorName(), Sector_.sectorName));
            }
            if (criteria.getEmployeesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getEmployeesId(), root -> root.join(Sector_.employees, JoinType.LEFT).get(Employee_.id))
                    );
            }
            if (criteria.getDepartmentsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getDepartmentsId(),
                            root -> root.join(Sector_.departments, JoinType.LEFT).get(Department_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
