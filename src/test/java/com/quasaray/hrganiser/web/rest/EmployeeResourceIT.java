package com.quasaray.hrganiser.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.quasaray.hrganiser.IntegrationTest;
import com.quasaray.hrganiser.domain.Department;
import com.quasaray.hrganiser.domain.Employee;
import com.quasaray.hrganiser.domain.Employee;
import com.quasaray.hrganiser.domain.HasSkill;
import com.quasaray.hrganiser.domain.Job;
import com.quasaray.hrganiser.domain.Location;
import com.quasaray.hrganiser.domain.Organisation;
import com.quasaray.hrganiser.domain.Sector;
import com.quasaray.hrganiser.domain.enumeration.JobLevel;
import com.quasaray.hrganiser.repository.EmployeeRepository;
import com.quasaray.hrganiser.repository.search.EmployeeSearchRepository;
import com.quasaray.hrganiser.service.criteria.EmployeeCriteria;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link EmployeeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EmployeeResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_KNOWN_AS = "AAAAAAAAAA";
    private static final String UPDATED_KNOWN_AS = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DOJ = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DOJ = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DOJ = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DOD = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DOD = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DOD = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DOB = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DOB = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DOB = LocalDate.ofEpochDay(-1L);

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final byte[] DEFAULT_RESUME = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_RESUME = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_RESUME_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_RESUME_CONTENT_TYPE = "image/png";

    private static final JobLevel DEFAULT_JOB_LEVEL = JobLevel.Junior;
    private static final JobLevel UPDATED_JOB_LEVEL = JobLevel.Senior;

    private static final String ENTITY_API_URL = "/api/employees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/employees";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * This repository is mocked in the com.quasaray.hrganiser.repository.search test package.
     *
     * @see com.quasaray.hrganiser.repository.search.EmployeeSearchRepositoryMockConfiguration
     */
    @Autowired
    private EmployeeSearchRepository mockEmployeeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEmployeeMockMvc;

    private Employee employee;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employee createEntity(EntityManager em) {
        Employee employee = new Employee()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .knownAs(DEFAULT_KNOWN_AS)
            .email(DEFAULT_EMAIL)
            .doj(DEFAULT_DOJ)
            .dod(DEFAULT_DOD)
            .dob(DEFAULT_DOB)
            .active(DEFAULT_ACTIVE)
            .resume(DEFAULT_RESUME)
            .resumeContentType(DEFAULT_RESUME_CONTENT_TYPE)
            .jobLevel(DEFAULT_JOB_LEVEL);
        return employee;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employee createUpdatedEntity(EntityManager em) {
        Employee employee = new Employee()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .knownAs(UPDATED_KNOWN_AS)
            .email(UPDATED_EMAIL)
            .doj(UPDATED_DOJ)
            .dod(UPDATED_DOD)
            .dob(UPDATED_DOB)
            .active(UPDATED_ACTIVE)
            .resume(UPDATED_RESUME)
            .resumeContentType(UPDATED_RESUME_CONTENT_TYPE)
            .jobLevel(UPDATED_JOB_LEVEL);
        return employee;
    }

    @BeforeEach
    public void initTest() {
        employee = createEntity(em);
    }

    @Test
    @Transactional
    void createEmployee() throws Exception {
        int databaseSizeBeforeCreate = employeeRepository.findAll().size();
        // Create the Employee
        restEmployeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employee)))
            .andExpect(status().isCreated());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeCreate + 1);
        Employee testEmployee = employeeList.get(employeeList.size() - 1);
        assertThat(testEmployee.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testEmployee.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testEmployee.getKnownAs()).isEqualTo(DEFAULT_KNOWN_AS);
        assertThat(testEmployee.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testEmployee.getDoj()).isEqualTo(DEFAULT_DOJ);
        assertThat(testEmployee.getDod()).isEqualTo(DEFAULT_DOD);
        assertThat(testEmployee.getDob()).isEqualTo(DEFAULT_DOB);
        assertThat(testEmployee.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testEmployee.getResume()).isEqualTo(DEFAULT_RESUME);
        assertThat(testEmployee.getResumeContentType()).isEqualTo(DEFAULT_RESUME_CONTENT_TYPE);
        assertThat(testEmployee.getJobLevel()).isEqualTo(DEFAULT_JOB_LEVEL);

        // Validate the Employee in Elasticsearch
        verify(mockEmployeeSearchRepository, times(1)).save(testEmployee);
    }

    @Test
    @Transactional
    void createEmployeeWithExistingId() throws Exception {
        // Create the Employee with an existing ID
        employee.setId(1L);

        int databaseSizeBeforeCreate = employeeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmployeeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employee)))
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeCreate);

        // Validate the Employee in Elasticsearch
        verify(mockEmployeeSearchRepository, times(0)).save(employee);
    }

    @Test
    @Transactional
    void getAllEmployees() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList
        restEmployeeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employee.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].knownAs").value(hasItem(DEFAULT_KNOWN_AS)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].doj").value(hasItem(DEFAULT_DOJ.toString())))
            .andExpect(jsonPath("$.[*].dod").value(hasItem(DEFAULT_DOD.toString())))
            .andExpect(jsonPath("$.[*].dob").value(hasItem(DEFAULT_DOB.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].resumeContentType").value(hasItem(DEFAULT_RESUME_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].resume").value(hasItem(Base64Utils.encodeToString(DEFAULT_RESUME))))
            .andExpect(jsonPath("$.[*].jobLevel").value(hasItem(DEFAULT_JOB_LEVEL.toString())));
    }

    @Test
    @Transactional
    void getEmployee() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get the employee
        restEmployeeMockMvc
            .perform(get(ENTITY_API_URL_ID, employee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(employee.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.knownAs").value(DEFAULT_KNOWN_AS))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.doj").value(DEFAULT_DOJ.toString()))
            .andExpect(jsonPath("$.dod").value(DEFAULT_DOD.toString()))
            .andExpect(jsonPath("$.dob").value(DEFAULT_DOB.toString()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.resumeContentType").value(DEFAULT_RESUME_CONTENT_TYPE))
            .andExpect(jsonPath("$.resume").value(Base64Utils.encodeToString(DEFAULT_RESUME)))
            .andExpect(jsonPath("$.jobLevel").value(DEFAULT_JOB_LEVEL.toString()));
    }

    @Test
    @Transactional
    void getEmployeesByIdFiltering() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        Long id = employee.getId();

        defaultEmployeeShouldBeFound("id.equals=" + id);
        defaultEmployeeShouldNotBeFound("id.notEquals=" + id);

        defaultEmployeeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEmployeeShouldNotBeFound("id.greaterThan=" + id);

        defaultEmployeeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEmployeeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEmployeesByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where firstName equals to DEFAULT_FIRST_NAME
        defaultEmployeeShouldBeFound("firstName.equals=" + DEFAULT_FIRST_NAME);

        // Get all the employeeList where firstName equals to UPDATED_FIRST_NAME
        defaultEmployeeShouldNotBeFound("firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByFirstNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where firstName not equals to DEFAULT_FIRST_NAME
        defaultEmployeeShouldNotBeFound("firstName.notEquals=" + DEFAULT_FIRST_NAME);

        // Get all the employeeList where firstName not equals to UPDATED_FIRST_NAME
        defaultEmployeeShouldBeFound("firstName.notEquals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where firstName in DEFAULT_FIRST_NAME or UPDATED_FIRST_NAME
        defaultEmployeeShouldBeFound("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME);

        // Get all the employeeList where firstName equals to UPDATED_FIRST_NAME
        defaultEmployeeShouldNotBeFound("firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where firstName is not null
        defaultEmployeeShouldBeFound("firstName.specified=true");

        // Get all the employeeList where firstName is null
        defaultEmployeeShouldNotBeFound("firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where firstName contains DEFAULT_FIRST_NAME
        defaultEmployeeShouldBeFound("firstName.contains=" + DEFAULT_FIRST_NAME);

        // Get all the employeeList where firstName contains UPDATED_FIRST_NAME
        defaultEmployeeShouldNotBeFound("firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where firstName does not contain DEFAULT_FIRST_NAME
        defaultEmployeeShouldNotBeFound("firstName.doesNotContain=" + DEFAULT_FIRST_NAME);

        // Get all the employeeList where firstName does not contain UPDATED_FIRST_NAME
        defaultEmployeeShouldBeFound("firstName.doesNotContain=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where lastName equals to DEFAULT_LAST_NAME
        defaultEmployeeShouldBeFound("lastName.equals=" + DEFAULT_LAST_NAME);

        // Get all the employeeList where lastName equals to UPDATED_LAST_NAME
        defaultEmployeeShouldNotBeFound("lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByLastNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where lastName not equals to DEFAULT_LAST_NAME
        defaultEmployeeShouldNotBeFound("lastName.notEquals=" + DEFAULT_LAST_NAME);

        // Get all the employeeList where lastName not equals to UPDATED_LAST_NAME
        defaultEmployeeShouldBeFound("lastName.notEquals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where lastName in DEFAULT_LAST_NAME or UPDATED_LAST_NAME
        defaultEmployeeShouldBeFound("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME);

        // Get all the employeeList where lastName equals to UPDATED_LAST_NAME
        defaultEmployeeShouldNotBeFound("lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where lastName is not null
        defaultEmployeeShouldBeFound("lastName.specified=true");

        // Get all the employeeList where lastName is null
        defaultEmployeeShouldNotBeFound("lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByLastNameContainsSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where lastName contains DEFAULT_LAST_NAME
        defaultEmployeeShouldBeFound("lastName.contains=" + DEFAULT_LAST_NAME);

        // Get all the employeeList where lastName contains UPDATED_LAST_NAME
        defaultEmployeeShouldNotBeFound("lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where lastName does not contain DEFAULT_LAST_NAME
        defaultEmployeeShouldNotBeFound("lastName.doesNotContain=" + DEFAULT_LAST_NAME);

        // Get all the employeeList where lastName does not contain UPDATED_LAST_NAME
        defaultEmployeeShouldBeFound("lastName.doesNotContain=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllEmployeesByKnownAsIsEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where knownAs equals to DEFAULT_KNOWN_AS
        defaultEmployeeShouldBeFound("knownAs.equals=" + DEFAULT_KNOWN_AS);

        // Get all the employeeList where knownAs equals to UPDATED_KNOWN_AS
        defaultEmployeeShouldNotBeFound("knownAs.equals=" + UPDATED_KNOWN_AS);
    }

    @Test
    @Transactional
    void getAllEmployeesByKnownAsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where knownAs not equals to DEFAULT_KNOWN_AS
        defaultEmployeeShouldNotBeFound("knownAs.notEquals=" + DEFAULT_KNOWN_AS);

        // Get all the employeeList where knownAs not equals to UPDATED_KNOWN_AS
        defaultEmployeeShouldBeFound("knownAs.notEquals=" + UPDATED_KNOWN_AS);
    }

    @Test
    @Transactional
    void getAllEmployeesByKnownAsIsInShouldWork() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where knownAs in DEFAULT_KNOWN_AS or UPDATED_KNOWN_AS
        defaultEmployeeShouldBeFound("knownAs.in=" + DEFAULT_KNOWN_AS + "," + UPDATED_KNOWN_AS);

        // Get all the employeeList where knownAs equals to UPDATED_KNOWN_AS
        defaultEmployeeShouldNotBeFound("knownAs.in=" + UPDATED_KNOWN_AS);
    }

    @Test
    @Transactional
    void getAllEmployeesByKnownAsIsNullOrNotNull() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where knownAs is not null
        defaultEmployeeShouldBeFound("knownAs.specified=true");

        // Get all the employeeList where knownAs is null
        defaultEmployeeShouldNotBeFound("knownAs.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByKnownAsContainsSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where knownAs contains DEFAULT_KNOWN_AS
        defaultEmployeeShouldBeFound("knownAs.contains=" + DEFAULT_KNOWN_AS);

        // Get all the employeeList where knownAs contains UPDATED_KNOWN_AS
        defaultEmployeeShouldNotBeFound("knownAs.contains=" + UPDATED_KNOWN_AS);
    }

    @Test
    @Transactional
    void getAllEmployeesByKnownAsNotContainsSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where knownAs does not contain DEFAULT_KNOWN_AS
        defaultEmployeeShouldNotBeFound("knownAs.doesNotContain=" + DEFAULT_KNOWN_AS);

        // Get all the employeeList where knownAs does not contain UPDATED_KNOWN_AS
        defaultEmployeeShouldBeFound("knownAs.doesNotContain=" + UPDATED_KNOWN_AS);
    }

    @Test
    @Transactional
    void getAllEmployeesByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where email equals to DEFAULT_EMAIL
        defaultEmployeeShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the employeeList where email equals to UPDATED_EMAIL
        defaultEmployeeShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllEmployeesByEmailIsNotEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where email not equals to DEFAULT_EMAIL
        defaultEmployeeShouldNotBeFound("email.notEquals=" + DEFAULT_EMAIL);

        // Get all the employeeList where email not equals to UPDATED_EMAIL
        defaultEmployeeShouldBeFound("email.notEquals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllEmployeesByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultEmployeeShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the employeeList where email equals to UPDATED_EMAIL
        defaultEmployeeShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllEmployeesByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where email is not null
        defaultEmployeeShouldBeFound("email.specified=true");

        // Get all the employeeList where email is null
        defaultEmployeeShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByEmailContainsSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where email contains DEFAULT_EMAIL
        defaultEmployeeShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the employeeList where email contains UPDATED_EMAIL
        defaultEmployeeShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllEmployeesByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where email does not contain DEFAULT_EMAIL
        defaultEmployeeShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the employeeList where email does not contain UPDATED_EMAIL
        defaultEmployeeShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllEmployeesByDojIsEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where doj equals to DEFAULT_DOJ
        defaultEmployeeShouldBeFound("doj.equals=" + DEFAULT_DOJ);

        // Get all the employeeList where doj equals to UPDATED_DOJ
        defaultEmployeeShouldNotBeFound("doj.equals=" + UPDATED_DOJ);
    }

    @Test
    @Transactional
    void getAllEmployeesByDojIsNotEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where doj not equals to DEFAULT_DOJ
        defaultEmployeeShouldNotBeFound("doj.notEquals=" + DEFAULT_DOJ);

        // Get all the employeeList where doj not equals to UPDATED_DOJ
        defaultEmployeeShouldBeFound("doj.notEquals=" + UPDATED_DOJ);
    }

    @Test
    @Transactional
    void getAllEmployeesByDojIsInShouldWork() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where doj in DEFAULT_DOJ or UPDATED_DOJ
        defaultEmployeeShouldBeFound("doj.in=" + DEFAULT_DOJ + "," + UPDATED_DOJ);

        // Get all the employeeList where doj equals to UPDATED_DOJ
        defaultEmployeeShouldNotBeFound("doj.in=" + UPDATED_DOJ);
    }

    @Test
    @Transactional
    void getAllEmployeesByDojIsNullOrNotNull() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where doj is not null
        defaultEmployeeShouldBeFound("doj.specified=true");

        // Get all the employeeList where doj is null
        defaultEmployeeShouldNotBeFound("doj.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByDojIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where doj is greater than or equal to DEFAULT_DOJ
        defaultEmployeeShouldBeFound("doj.greaterThanOrEqual=" + DEFAULT_DOJ);

        // Get all the employeeList where doj is greater than or equal to UPDATED_DOJ
        defaultEmployeeShouldNotBeFound("doj.greaterThanOrEqual=" + UPDATED_DOJ);
    }

    @Test
    @Transactional
    void getAllEmployeesByDojIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where doj is less than or equal to DEFAULT_DOJ
        defaultEmployeeShouldBeFound("doj.lessThanOrEqual=" + DEFAULT_DOJ);

        // Get all the employeeList where doj is less than or equal to SMALLER_DOJ
        defaultEmployeeShouldNotBeFound("doj.lessThanOrEqual=" + SMALLER_DOJ);
    }

    @Test
    @Transactional
    void getAllEmployeesByDojIsLessThanSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where doj is less than DEFAULT_DOJ
        defaultEmployeeShouldNotBeFound("doj.lessThan=" + DEFAULT_DOJ);

        // Get all the employeeList where doj is less than UPDATED_DOJ
        defaultEmployeeShouldBeFound("doj.lessThan=" + UPDATED_DOJ);
    }

    @Test
    @Transactional
    void getAllEmployeesByDojIsGreaterThanSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where doj is greater than DEFAULT_DOJ
        defaultEmployeeShouldNotBeFound("doj.greaterThan=" + DEFAULT_DOJ);

        // Get all the employeeList where doj is greater than SMALLER_DOJ
        defaultEmployeeShouldBeFound("doj.greaterThan=" + SMALLER_DOJ);
    }

    @Test
    @Transactional
    void getAllEmployeesByDodIsEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where dod equals to DEFAULT_DOD
        defaultEmployeeShouldBeFound("dod.equals=" + DEFAULT_DOD);

        // Get all the employeeList where dod equals to UPDATED_DOD
        defaultEmployeeShouldNotBeFound("dod.equals=" + UPDATED_DOD);
    }

    @Test
    @Transactional
    void getAllEmployeesByDodIsNotEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where dod not equals to DEFAULT_DOD
        defaultEmployeeShouldNotBeFound("dod.notEquals=" + DEFAULT_DOD);

        // Get all the employeeList where dod not equals to UPDATED_DOD
        defaultEmployeeShouldBeFound("dod.notEquals=" + UPDATED_DOD);
    }

    @Test
    @Transactional
    void getAllEmployeesByDodIsInShouldWork() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where dod in DEFAULT_DOD or UPDATED_DOD
        defaultEmployeeShouldBeFound("dod.in=" + DEFAULT_DOD + "," + UPDATED_DOD);

        // Get all the employeeList where dod equals to UPDATED_DOD
        defaultEmployeeShouldNotBeFound("dod.in=" + UPDATED_DOD);
    }

    @Test
    @Transactional
    void getAllEmployeesByDodIsNullOrNotNull() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where dod is not null
        defaultEmployeeShouldBeFound("dod.specified=true");

        // Get all the employeeList where dod is null
        defaultEmployeeShouldNotBeFound("dod.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByDodIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where dod is greater than or equal to DEFAULT_DOD
        defaultEmployeeShouldBeFound("dod.greaterThanOrEqual=" + DEFAULT_DOD);

        // Get all the employeeList where dod is greater than or equal to UPDATED_DOD
        defaultEmployeeShouldNotBeFound("dod.greaterThanOrEqual=" + UPDATED_DOD);
    }

    @Test
    @Transactional
    void getAllEmployeesByDodIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where dod is less than or equal to DEFAULT_DOD
        defaultEmployeeShouldBeFound("dod.lessThanOrEqual=" + DEFAULT_DOD);

        // Get all the employeeList where dod is less than or equal to SMALLER_DOD
        defaultEmployeeShouldNotBeFound("dod.lessThanOrEqual=" + SMALLER_DOD);
    }

    @Test
    @Transactional
    void getAllEmployeesByDodIsLessThanSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where dod is less than DEFAULT_DOD
        defaultEmployeeShouldNotBeFound("dod.lessThan=" + DEFAULT_DOD);

        // Get all the employeeList where dod is less than UPDATED_DOD
        defaultEmployeeShouldBeFound("dod.lessThan=" + UPDATED_DOD);
    }

    @Test
    @Transactional
    void getAllEmployeesByDodIsGreaterThanSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where dod is greater than DEFAULT_DOD
        defaultEmployeeShouldNotBeFound("dod.greaterThan=" + DEFAULT_DOD);

        // Get all the employeeList where dod is greater than SMALLER_DOD
        defaultEmployeeShouldBeFound("dod.greaterThan=" + SMALLER_DOD);
    }

    @Test
    @Transactional
    void getAllEmployeesByDobIsEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where dob equals to DEFAULT_DOB
        defaultEmployeeShouldBeFound("dob.equals=" + DEFAULT_DOB);

        // Get all the employeeList where dob equals to UPDATED_DOB
        defaultEmployeeShouldNotBeFound("dob.equals=" + UPDATED_DOB);
    }

    @Test
    @Transactional
    void getAllEmployeesByDobIsNotEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where dob not equals to DEFAULT_DOB
        defaultEmployeeShouldNotBeFound("dob.notEquals=" + DEFAULT_DOB);

        // Get all the employeeList where dob not equals to UPDATED_DOB
        defaultEmployeeShouldBeFound("dob.notEquals=" + UPDATED_DOB);
    }

    @Test
    @Transactional
    void getAllEmployeesByDobIsInShouldWork() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where dob in DEFAULT_DOB or UPDATED_DOB
        defaultEmployeeShouldBeFound("dob.in=" + DEFAULT_DOB + "," + UPDATED_DOB);

        // Get all the employeeList where dob equals to UPDATED_DOB
        defaultEmployeeShouldNotBeFound("dob.in=" + UPDATED_DOB);
    }

    @Test
    @Transactional
    void getAllEmployeesByDobIsNullOrNotNull() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where dob is not null
        defaultEmployeeShouldBeFound("dob.specified=true");

        // Get all the employeeList where dob is null
        defaultEmployeeShouldNotBeFound("dob.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByDobIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where dob is greater than or equal to DEFAULT_DOB
        defaultEmployeeShouldBeFound("dob.greaterThanOrEqual=" + DEFAULT_DOB);

        // Get all the employeeList where dob is greater than or equal to UPDATED_DOB
        defaultEmployeeShouldNotBeFound("dob.greaterThanOrEqual=" + UPDATED_DOB);
    }

    @Test
    @Transactional
    void getAllEmployeesByDobIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where dob is less than or equal to DEFAULT_DOB
        defaultEmployeeShouldBeFound("dob.lessThanOrEqual=" + DEFAULT_DOB);

        // Get all the employeeList where dob is less than or equal to SMALLER_DOB
        defaultEmployeeShouldNotBeFound("dob.lessThanOrEqual=" + SMALLER_DOB);
    }

    @Test
    @Transactional
    void getAllEmployeesByDobIsLessThanSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where dob is less than DEFAULT_DOB
        defaultEmployeeShouldNotBeFound("dob.lessThan=" + DEFAULT_DOB);

        // Get all the employeeList where dob is less than UPDATED_DOB
        defaultEmployeeShouldBeFound("dob.lessThan=" + UPDATED_DOB);
    }

    @Test
    @Transactional
    void getAllEmployeesByDobIsGreaterThanSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where dob is greater than DEFAULT_DOB
        defaultEmployeeShouldNotBeFound("dob.greaterThan=" + DEFAULT_DOB);

        // Get all the employeeList where dob is greater than SMALLER_DOB
        defaultEmployeeShouldBeFound("dob.greaterThan=" + SMALLER_DOB);
    }

    @Test
    @Transactional
    void getAllEmployeesByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where active equals to DEFAULT_ACTIVE
        defaultEmployeeShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the employeeList where active equals to UPDATED_ACTIVE
        defaultEmployeeShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllEmployeesByActiveIsNotEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where active not equals to DEFAULT_ACTIVE
        defaultEmployeeShouldNotBeFound("active.notEquals=" + DEFAULT_ACTIVE);

        // Get all the employeeList where active not equals to UPDATED_ACTIVE
        defaultEmployeeShouldBeFound("active.notEquals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllEmployeesByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultEmployeeShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the employeeList where active equals to UPDATED_ACTIVE
        defaultEmployeeShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllEmployeesByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where active is not null
        defaultEmployeeShouldBeFound("active.specified=true");

        // Get all the employeeList where active is null
        defaultEmployeeShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesByJobLevelIsEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where jobLevel equals to DEFAULT_JOB_LEVEL
        defaultEmployeeShouldBeFound("jobLevel.equals=" + DEFAULT_JOB_LEVEL);

        // Get all the employeeList where jobLevel equals to UPDATED_JOB_LEVEL
        defaultEmployeeShouldNotBeFound("jobLevel.equals=" + UPDATED_JOB_LEVEL);
    }

    @Test
    @Transactional
    void getAllEmployeesByJobLevelIsNotEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where jobLevel not equals to DEFAULT_JOB_LEVEL
        defaultEmployeeShouldNotBeFound("jobLevel.notEquals=" + DEFAULT_JOB_LEVEL);

        // Get all the employeeList where jobLevel not equals to UPDATED_JOB_LEVEL
        defaultEmployeeShouldBeFound("jobLevel.notEquals=" + UPDATED_JOB_LEVEL);
    }

    @Test
    @Transactional
    void getAllEmployeesByJobLevelIsInShouldWork() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where jobLevel in DEFAULT_JOB_LEVEL or UPDATED_JOB_LEVEL
        defaultEmployeeShouldBeFound("jobLevel.in=" + DEFAULT_JOB_LEVEL + "," + UPDATED_JOB_LEVEL);

        // Get all the employeeList where jobLevel equals to UPDATED_JOB_LEVEL
        defaultEmployeeShouldNotBeFound("jobLevel.in=" + UPDATED_JOB_LEVEL);
    }

    @Test
    @Transactional
    void getAllEmployeesByJobLevelIsNullOrNotNull() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        // Get all the employeeList where jobLevel is not null
        defaultEmployeeShouldBeFound("jobLevel.specified=true");

        // Get all the employeeList where jobLevel is null
        defaultEmployeeShouldNotBeFound("jobLevel.specified=false");
    }

    @Test
    @Transactional
    void getAllEmployeesBySkillsIsEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);
        HasSkill skills;
        if (TestUtil.findAll(em, HasSkill.class).isEmpty()) {
            skills = HasSkillResourceIT.createEntity(em);
            em.persist(skills);
            em.flush();
        } else {
            skills = TestUtil.findAll(em, HasSkill.class).get(0);
        }
        em.persist(skills);
        em.flush();
        employee.addSkills(skills);
        employeeRepository.saveAndFlush(employee);
        Long skillsId = skills.getId();

        // Get all the employeeList where skills equals to skillsId
        defaultEmployeeShouldBeFound("skillsId.equals=" + skillsId);

        // Get all the employeeList where skills equals to (skillsId + 1)
        defaultEmployeeShouldNotBeFound("skillsId.equals=" + (skillsId + 1));
    }

    @Test
    @Transactional
    void getAllEmployeesByManagerIsEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);
        Employee manager;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            manager = EmployeeResourceIT.createEntity(em);
            em.persist(manager);
            em.flush();
        } else {
            manager = TestUtil.findAll(em, Employee.class).get(0);
        }
        em.persist(manager);
        em.flush();
        employee.setManager(manager);
        employeeRepository.saveAndFlush(employee);
        Long managerId = manager.getId();

        // Get all the employeeList where manager equals to managerId
        defaultEmployeeShouldBeFound("managerId.equals=" + managerId);

        // Get all the employeeList where manager equals to (managerId + 1)
        defaultEmployeeShouldNotBeFound("managerId.equals=" + (managerId + 1));
    }

    @Test
    @Transactional
    void getAllEmployeesByOrgIsEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);
        Organisation org;
        if (TestUtil.findAll(em, Organisation.class).isEmpty()) {
            org = OrganisationResourceIT.createEntity(em);
            em.persist(org);
            em.flush();
        } else {
            org = TestUtil.findAll(em, Organisation.class).get(0);
        }
        em.persist(org);
        em.flush();
        employee.setOrg(org);
        employeeRepository.saveAndFlush(employee);
        Long orgId = org.getId();

        // Get all the employeeList where org equals to orgId
        defaultEmployeeShouldBeFound("orgId.equals=" + orgId);

        // Get all the employeeList where org equals to (orgId + 1)
        defaultEmployeeShouldNotBeFound("orgId.equals=" + (orgId + 1));
    }

    @Test
    @Transactional
    void getAllEmployeesByLocIsEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);
        Location loc;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            loc = LocationResourceIT.createEntity(em);
            em.persist(loc);
            em.flush();
        } else {
            loc = TestUtil.findAll(em, Location.class).get(0);
        }
        em.persist(loc);
        em.flush();
        employee.setLoc(loc);
        employeeRepository.saveAndFlush(employee);
        Long locId = loc.getId();

        // Get all the employeeList where loc equals to locId
        defaultEmployeeShouldBeFound("locId.equals=" + locId);

        // Get all the employeeList where loc equals to (locId + 1)
        defaultEmployeeShouldNotBeFound("locId.equals=" + (locId + 1));
    }

    @Test
    @Transactional
    void getAllEmployeesByDeptIsEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);
        Department dept;
        if (TestUtil.findAll(em, Department.class).isEmpty()) {
            dept = DepartmentResourceIT.createEntity(em);
            em.persist(dept);
            em.flush();
        } else {
            dept = TestUtil.findAll(em, Department.class).get(0);
        }
        em.persist(dept);
        em.flush();
        employee.setDept(dept);
        employeeRepository.saveAndFlush(employee);
        Long deptId = dept.getId();

        // Get all the employeeList where dept equals to deptId
        defaultEmployeeShouldBeFound("deptId.equals=" + deptId);

        // Get all the employeeList where dept equals to (deptId + 1)
        defaultEmployeeShouldNotBeFound("deptId.equals=" + (deptId + 1));
    }

    @Test
    @Transactional
    void getAllEmployeesBySectIsEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);
        Sector sect;
        if (TestUtil.findAll(em, Sector.class).isEmpty()) {
            sect = SectorResourceIT.createEntity(em);
            em.persist(sect);
            em.flush();
        } else {
            sect = TestUtil.findAll(em, Sector.class).get(0);
        }
        em.persist(sect);
        em.flush();
        employee.setSect(sect);
        employeeRepository.saveAndFlush(employee);
        Long sectId = sect.getId();

        // Get all the employeeList where sect equals to sectId
        defaultEmployeeShouldBeFound("sectId.equals=" + sectId);

        // Get all the employeeList where sect equals to (sectId + 1)
        defaultEmployeeShouldNotBeFound("sectId.equals=" + (sectId + 1));
    }

    @Test
    @Transactional
    void getAllEmployeesByJobIsEqualToSomething() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);
        Job job;
        if (TestUtil.findAll(em, Job.class).isEmpty()) {
            job = JobResourceIT.createEntity(em);
            em.persist(job);
            em.flush();
        } else {
            job = TestUtil.findAll(em, Job.class).get(0);
        }
        em.persist(job);
        em.flush();
        employee.setJob(job);
        employeeRepository.saveAndFlush(employee);
        Long jobId = job.getId();

        // Get all the employeeList where job equals to jobId
        defaultEmployeeShouldBeFound("jobId.equals=" + jobId);

        // Get all the employeeList where job equals to (jobId + 1)
        defaultEmployeeShouldNotBeFound("jobId.equals=" + (jobId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEmployeeShouldBeFound(String filter) throws Exception {
        restEmployeeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employee.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].knownAs").value(hasItem(DEFAULT_KNOWN_AS)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].doj").value(hasItem(DEFAULT_DOJ.toString())))
            .andExpect(jsonPath("$.[*].dod").value(hasItem(DEFAULT_DOD.toString())))
            .andExpect(jsonPath("$.[*].dob").value(hasItem(DEFAULT_DOB.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].resumeContentType").value(hasItem(DEFAULT_RESUME_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].resume").value(hasItem(Base64Utils.encodeToString(DEFAULT_RESUME))))
            .andExpect(jsonPath("$.[*].jobLevel").value(hasItem(DEFAULT_JOB_LEVEL.toString())));

        // Check, that the count call also returns 1
        restEmployeeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEmployeeShouldNotBeFound(String filter) throws Exception {
        restEmployeeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEmployeeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEmployee() throws Exception {
        // Get the employee
        restEmployeeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewEmployee() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();

        // Update the employee
        Employee updatedEmployee = employeeRepository.findById(employee.getId()).get();
        // Disconnect from session so that the updates on updatedEmployee are not directly saved in db
        em.detach(updatedEmployee);
        updatedEmployee
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .knownAs(UPDATED_KNOWN_AS)
            .email(UPDATED_EMAIL)
            .doj(UPDATED_DOJ)
            .dod(UPDATED_DOD)
            .dob(UPDATED_DOB)
            .active(UPDATED_ACTIVE)
            .resume(UPDATED_RESUME)
            .resumeContentType(UPDATED_RESUME_CONTENT_TYPE)
            .jobLevel(UPDATED_JOB_LEVEL);

        restEmployeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEmployee.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedEmployee))
            )
            .andExpect(status().isOk());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);
        Employee testEmployee = employeeList.get(employeeList.size() - 1);
        assertThat(testEmployee.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testEmployee.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testEmployee.getKnownAs()).isEqualTo(UPDATED_KNOWN_AS);
        assertThat(testEmployee.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testEmployee.getDoj()).isEqualTo(UPDATED_DOJ);
        assertThat(testEmployee.getDod()).isEqualTo(UPDATED_DOD);
        assertThat(testEmployee.getDob()).isEqualTo(UPDATED_DOB);
        assertThat(testEmployee.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testEmployee.getResume()).isEqualTo(UPDATED_RESUME);
        assertThat(testEmployee.getResumeContentType()).isEqualTo(UPDATED_RESUME_CONTENT_TYPE);
        assertThat(testEmployee.getJobLevel()).isEqualTo(UPDATED_JOB_LEVEL);

        // Validate the Employee in Elasticsearch
        verify(mockEmployeeSearchRepository).save(testEmployee);
    }

    @Test
    @Transactional
    void putNonExistingEmployee() throws Exception {
        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();
        employee.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, employee.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employee))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Employee in Elasticsearch
        verify(mockEmployeeSearchRepository, times(0)).save(employee);
    }

    @Test
    @Transactional
    void putWithIdMismatchEmployee() throws Exception {
        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();
        employee.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(employee))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Employee in Elasticsearch
        verify(mockEmployeeSearchRepository, times(0)).save(employee);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEmployee() throws Exception {
        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();
        employee.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(employee)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Employee in Elasticsearch
        verify(mockEmployeeSearchRepository, times(0)).save(employee);
    }

    @Test
    @Transactional
    void partialUpdateEmployeeWithPatch() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();

        // Update the employee using partial update
        Employee partialUpdatedEmployee = new Employee();
        partialUpdatedEmployee.setId(employee.getId());

        partialUpdatedEmployee
            .lastName(UPDATED_LAST_NAME)
            .knownAs(UPDATED_KNOWN_AS)
            .dob(UPDATED_DOB)
            .active(UPDATED_ACTIVE)
            .resume(UPDATED_RESUME)
            .resumeContentType(UPDATED_RESUME_CONTENT_TYPE)
            .jobLevel(UPDATED_JOB_LEVEL);

        restEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmployee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmployee))
            )
            .andExpect(status().isOk());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);
        Employee testEmployee = employeeList.get(employeeList.size() - 1);
        assertThat(testEmployee.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testEmployee.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testEmployee.getKnownAs()).isEqualTo(UPDATED_KNOWN_AS);
        assertThat(testEmployee.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testEmployee.getDoj()).isEqualTo(DEFAULT_DOJ);
        assertThat(testEmployee.getDod()).isEqualTo(DEFAULT_DOD);
        assertThat(testEmployee.getDob()).isEqualTo(UPDATED_DOB);
        assertThat(testEmployee.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testEmployee.getResume()).isEqualTo(UPDATED_RESUME);
        assertThat(testEmployee.getResumeContentType()).isEqualTo(UPDATED_RESUME_CONTENT_TYPE);
        assertThat(testEmployee.getJobLevel()).isEqualTo(UPDATED_JOB_LEVEL);
    }

    @Test
    @Transactional
    void fullUpdateEmployeeWithPatch() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();

        // Update the employee using partial update
        Employee partialUpdatedEmployee = new Employee();
        partialUpdatedEmployee.setId(employee.getId());

        partialUpdatedEmployee
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .knownAs(UPDATED_KNOWN_AS)
            .email(UPDATED_EMAIL)
            .doj(UPDATED_DOJ)
            .dod(UPDATED_DOD)
            .dob(UPDATED_DOB)
            .active(UPDATED_ACTIVE)
            .resume(UPDATED_RESUME)
            .resumeContentType(UPDATED_RESUME_CONTENT_TYPE)
            .jobLevel(UPDATED_JOB_LEVEL);

        restEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmployee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmployee))
            )
            .andExpect(status().isOk());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);
        Employee testEmployee = employeeList.get(employeeList.size() - 1);
        assertThat(testEmployee.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testEmployee.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testEmployee.getKnownAs()).isEqualTo(UPDATED_KNOWN_AS);
        assertThat(testEmployee.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testEmployee.getDoj()).isEqualTo(UPDATED_DOJ);
        assertThat(testEmployee.getDod()).isEqualTo(UPDATED_DOD);
        assertThat(testEmployee.getDob()).isEqualTo(UPDATED_DOB);
        assertThat(testEmployee.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testEmployee.getResume()).isEqualTo(UPDATED_RESUME);
        assertThat(testEmployee.getResumeContentType()).isEqualTo(UPDATED_RESUME_CONTENT_TYPE);
        assertThat(testEmployee.getJobLevel()).isEqualTo(UPDATED_JOB_LEVEL);
    }

    @Test
    @Transactional
    void patchNonExistingEmployee() throws Exception {
        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();
        employee.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, employee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(employee))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Employee in Elasticsearch
        verify(mockEmployeeSearchRepository, times(0)).save(employee);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEmployee() throws Exception {
        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();
        employee.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(employee))
            )
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Employee in Elasticsearch
        verify(mockEmployeeSearchRepository, times(0)).save(employee);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEmployee() throws Exception {
        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();
        employee.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmployeeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(employee)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Employee in Elasticsearch
        verify(mockEmployeeSearchRepository, times(0)).save(employee);
    }

    @Test
    @Transactional
    void deleteEmployee() throws Exception {
        // Initialize the database
        employeeRepository.saveAndFlush(employee);

        int databaseSizeBeforeDelete = employeeRepository.findAll().size();

        // Delete the employee
        restEmployeeMockMvc
            .perform(delete(ENTITY_API_URL_ID, employee.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Employee in Elasticsearch
        verify(mockEmployeeSearchRepository, times(1)).deleteById(employee.getId());
    }

    @Test
    @Transactional
    void searchEmployee() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        employeeRepository.saveAndFlush(employee);
        when(mockEmployeeSearchRepository.search("id:" + employee.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(employee), PageRequest.of(0, 1), 1));

        // Search the employee
        restEmployeeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + employee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(employee.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].knownAs").value(hasItem(DEFAULT_KNOWN_AS)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].doj").value(hasItem(DEFAULT_DOJ.toString())))
            .andExpect(jsonPath("$.[*].dod").value(hasItem(DEFAULT_DOD.toString())))
            .andExpect(jsonPath("$.[*].dob").value(hasItem(DEFAULT_DOB.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].resumeContentType").value(hasItem(DEFAULT_RESUME_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].resume").value(hasItem(Base64Utils.encodeToString(DEFAULT_RESUME))))
            .andExpect(jsonPath("$.[*].jobLevel").value(hasItem(DEFAULT_JOB_LEVEL.toString())));
    }
}
