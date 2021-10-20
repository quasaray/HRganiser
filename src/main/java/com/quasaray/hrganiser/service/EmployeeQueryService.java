package com.quasaray.hrganiser.service;

import com.quasaray.hrganiser.domain.*; // for static metamodels
import com.quasaray.hrganiser.domain.Employee;
import com.quasaray.hrganiser.repository.EmployeeRepository;
import com.quasaray.hrganiser.repository.search.EmployeeSearchRepository;
import com.quasaray.hrganiser.service.criteria.EmployeeCriteria;
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
 * Service for executing complex queries for {@link Employee} entities in the database.
 * The main input is a {@link EmployeeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Employee} or a {@link Page} of {@link Employee} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EmployeeQueryService extends QueryService<Employee> {

    private final Logger log = LoggerFactory.getLogger(EmployeeQueryService.class);

    private final EmployeeRepository employeeRepository;

    private final EmployeeSearchRepository employeeSearchRepository;

    public EmployeeQueryService(EmployeeRepository employeeRepository, EmployeeSearchRepository employeeSearchRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeSearchRepository = employeeSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Employee} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Employee> findByCriteria(EmployeeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Employee> specification = createSpecification(criteria);
        return employeeRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Employee} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Employee> findByCriteria(EmployeeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Employee> specification = createSpecification(criteria);
        return employeeRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EmployeeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Employee> specification = createSpecification(criteria);
        return employeeRepository.count(specification);
    }

    /**
     * Function to convert {@link EmployeeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Employee> createSpecification(EmployeeCriteria criteria) {
        Specification<Employee> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Employee_.id));
            }
            if (criteria.getFirstName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstName(), Employee_.firstName));
            }
            if (criteria.getLastName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastName(), Employee_.lastName));
            }
            if (criteria.getKnownAs() != null) {
                specification = specification.and(buildStringSpecification(criteria.getKnownAs(), Employee_.knownAs));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), Employee_.email));
            }
            if (criteria.getDoj() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDoj(), Employee_.doj));
            }
            if (criteria.getDod() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDod(), Employee_.dod));
            }
            if (criteria.getDob() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDob(), Employee_.dob));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(buildSpecification(criteria.getActive(), Employee_.active));
            }
            if (criteria.getJobLevel() != null) {
                specification = specification.and(buildSpecification(criteria.getJobLevel(), Employee_.jobLevel));
            }
            if (criteria.getSkillsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getSkillsId(), root -> root.join(Employee_.skills, JoinType.LEFT).get(HasSkill_.id))
                    );
            }
            if (criteria.getManagerId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getManagerId(), root -> root.join(Employee_.manager, JoinType.LEFT).get(Employee_.id))
                    );
            }
            if (criteria.getOrgId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getOrgId(), root -> root.join(Employee_.org, JoinType.LEFT).get(Organisation_.id))
                    );
            }
            if (criteria.getLocId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getLocId(), root -> root.join(Employee_.loc, JoinType.LEFT).get(Location_.id))
                    );
            }
            if (criteria.getDeptId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getDeptId(), root -> root.join(Employee_.dept, JoinType.LEFT).get(Department_.id))
                    );
            }
            if (criteria.getSectId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getSectId(), root -> root.join(Employee_.sect, JoinType.LEFT).get(Sector_.id))
                    );
            }
            if (criteria.getJobId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getJobId(), root -> root.join(Employee_.job, JoinType.LEFT).get(Job_.id))
                    );
            }
        }
        return specification;
    }
}
