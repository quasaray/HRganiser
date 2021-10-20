package com.quasaray.hrganiser.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.quasaray.hrganiser.domain.Employee;
import com.quasaray.hrganiser.repository.EmployeeRepository;
import com.quasaray.hrganiser.repository.search.EmployeeSearchRepository;
import com.quasaray.hrganiser.service.EmployeeService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Employee}.
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;

    private final EmployeeSearchRepository employeeSearchRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeSearchRepository employeeSearchRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeSearchRepository = employeeSearchRepository;
    }

    @Override
    public Employee save(Employee employee) {
        log.debug("Request to save Employee : {}", employee);
        Employee result = employeeRepository.save(employee);
        employeeSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Employee> partialUpdate(Employee employee) {
        log.debug("Request to partially update Employee : {}", employee);

        return employeeRepository
            .findById(employee.getId())
            .map(existingEmployee -> {
                if (employee.getFirstName() != null) {
                    existingEmployee.setFirstName(employee.getFirstName());
                }
                if (employee.getLastName() != null) {
                    existingEmployee.setLastName(employee.getLastName());
                }
                if (employee.getKnownAs() != null) {
                    existingEmployee.setKnownAs(employee.getKnownAs());
                }
                if (employee.getEmail() != null) {
                    existingEmployee.setEmail(employee.getEmail());
                }
                if (employee.getDoj() != null) {
                    existingEmployee.setDoj(employee.getDoj());
                }
                if (employee.getDod() != null) {
                    existingEmployee.setDod(employee.getDod());
                }
                if (employee.getDob() != null) {
                    existingEmployee.setDob(employee.getDob());
                }
                if (employee.getActive() != null) {
                    existingEmployee.setActive(employee.getActive());
                }
                if (employee.getResume() != null) {
                    existingEmployee.setResume(employee.getResume());
                }
                if (employee.getResumeContentType() != null) {
                    existingEmployee.setResumeContentType(employee.getResumeContentType());
                }
                if (employee.getJobLevel() != null) {
                    existingEmployee.setJobLevel(employee.getJobLevel());
                }

                return existingEmployee;
            })
            .map(employeeRepository::save)
            .map(savedEmployee -> {
                employeeSearchRepository.save(savedEmployee);

                return savedEmployee;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Employee> findAll(Pageable pageable) {
        log.debug("Request to get all Employees");
        return employeeRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Employee> findOne(Long id) {
        log.debug("Request to get Employee : {}", id);
        return employeeRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Employee : {}", id);
        employeeRepository.deleteById(id);
        employeeSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Employee> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Employees for query {}", query);
        return employeeSearchRepository.search(query, pageable);
    }
}
