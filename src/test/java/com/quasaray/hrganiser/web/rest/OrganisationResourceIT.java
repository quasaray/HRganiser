package com.quasaray.hrganiser.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.quasaray.hrganiser.IntegrationTest;
import com.quasaray.hrganiser.domain.Employee;
import com.quasaray.hrganiser.domain.Organisation;
import com.quasaray.hrganiser.domain.User;
import com.quasaray.hrganiser.repository.OrganisationRepository;
import com.quasaray.hrganiser.repository.search.OrganisationSearchRepository;
import com.quasaray.hrganiser.service.OrganisationService;
import com.quasaray.hrganiser.service.criteria.OrganisationCriteria;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
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

/**
 * Integration tests for the {@link OrganisationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OrganisationResourceIT {

    private static final String DEFAULT_ORG_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ORG_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/organisations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/organisations";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrganisationRepository organisationRepository;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private OrganisationService organisationServiceMock;

    /**
     * This repository is mocked in the com.quasaray.hrganiser.repository.search test package.
     *
     * @see com.quasaray.hrganiser.repository.search.OrganisationSearchRepositoryMockConfiguration
     */
    @Autowired
    private OrganisationSearchRepository mockOrganisationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrganisationMockMvc;

    private Organisation organisation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organisation createEntity(EntityManager em) {
        Organisation organisation = new Organisation().orgName(DEFAULT_ORG_NAME);
        return organisation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organisation createUpdatedEntity(EntityManager em) {
        Organisation organisation = new Organisation().orgName(UPDATED_ORG_NAME);
        return organisation;
    }

    @BeforeEach
    public void initTest() {
        organisation = createEntity(em);
    }

    @Test
    @Transactional
    void createOrganisation() throws Exception {
        int databaseSizeBeforeCreate = organisationRepository.findAll().size();
        // Create the Organisation
        restOrganisationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organisation)))
            .andExpect(status().isCreated());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeCreate + 1);
        Organisation testOrganisation = organisationList.get(organisationList.size() - 1);
        assertThat(testOrganisation.getOrgName()).isEqualTo(DEFAULT_ORG_NAME);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository, times(1)).save(testOrganisation);
    }

    @Test
    @Transactional
    void createOrganisationWithExistingId() throws Exception {
        // Create the Organisation with an existing ID
        organisation.setId(1L);

        int databaseSizeBeforeCreate = organisationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrganisationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organisation)))
            .andExpect(status().isBadRequest());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeCreate);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository, times(0)).save(organisation);
    }

    @Test
    @Transactional
    void checkOrgNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = organisationRepository.findAll().size();
        // set the field null
        organisation.setOrgName(null);

        // Create the Organisation, which fails.

        restOrganisationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organisation)))
            .andExpect(status().isBadRequest());

        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOrganisations() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList
        restOrganisationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organisation.getId().intValue())))
            .andExpect(jsonPath("$.[*].orgName").value(hasItem(DEFAULT_ORG_NAME)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrganisationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(organisationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrganisationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(organisationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOrganisationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(organisationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOrganisationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(organisationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getOrganisation() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get the organisation
        restOrganisationMockMvc
            .perform(get(ENTITY_API_URL_ID, organisation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(organisation.getId().intValue()))
            .andExpect(jsonPath("$.orgName").value(DEFAULT_ORG_NAME));
    }

    @Test
    @Transactional
    void getOrganisationsByIdFiltering() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        Long id = organisation.getId();

        defaultOrganisationShouldBeFound("id.equals=" + id);
        defaultOrganisationShouldNotBeFound("id.notEquals=" + id);

        defaultOrganisationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOrganisationShouldNotBeFound("id.greaterThan=" + id);

        defaultOrganisationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOrganisationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrganisationsByOrgNameIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where orgName equals to DEFAULT_ORG_NAME
        defaultOrganisationShouldBeFound("orgName.equals=" + DEFAULT_ORG_NAME);

        // Get all the organisationList where orgName equals to UPDATED_ORG_NAME
        defaultOrganisationShouldNotBeFound("orgName.equals=" + UPDATED_ORG_NAME);
    }

    @Test
    @Transactional
    void getAllOrganisationsByOrgNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where orgName not equals to DEFAULT_ORG_NAME
        defaultOrganisationShouldNotBeFound("orgName.notEquals=" + DEFAULT_ORG_NAME);

        // Get all the organisationList where orgName not equals to UPDATED_ORG_NAME
        defaultOrganisationShouldBeFound("orgName.notEquals=" + UPDATED_ORG_NAME);
    }

    @Test
    @Transactional
    void getAllOrganisationsByOrgNameIsInShouldWork() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where orgName in DEFAULT_ORG_NAME or UPDATED_ORG_NAME
        defaultOrganisationShouldBeFound("orgName.in=" + DEFAULT_ORG_NAME + "," + UPDATED_ORG_NAME);

        // Get all the organisationList where orgName equals to UPDATED_ORG_NAME
        defaultOrganisationShouldNotBeFound("orgName.in=" + UPDATED_ORG_NAME);
    }

    @Test
    @Transactional
    void getAllOrganisationsByOrgNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where orgName is not null
        defaultOrganisationShouldBeFound("orgName.specified=true");

        // Get all the organisationList where orgName is null
        defaultOrganisationShouldNotBeFound("orgName.specified=false");
    }

    @Test
    @Transactional
    void getAllOrganisationsByOrgNameContainsSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where orgName contains DEFAULT_ORG_NAME
        defaultOrganisationShouldBeFound("orgName.contains=" + DEFAULT_ORG_NAME);

        // Get all the organisationList where orgName contains UPDATED_ORG_NAME
        defaultOrganisationShouldNotBeFound("orgName.contains=" + UPDATED_ORG_NAME);
    }

    @Test
    @Transactional
    void getAllOrganisationsByOrgNameNotContainsSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        // Get all the organisationList where orgName does not contain DEFAULT_ORG_NAME
        defaultOrganisationShouldNotBeFound("orgName.doesNotContain=" + DEFAULT_ORG_NAME);

        // Get all the organisationList where orgName does not contain UPDATED_ORG_NAME
        defaultOrganisationShouldBeFound("orgName.doesNotContain=" + UPDATED_ORG_NAME);
    }

    @Test
    @Transactional
    void getAllOrganisationsByStaffIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);
        User staff;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            staff = UserResourceIT.createEntity(em);
            em.persist(staff);
            em.flush();
        } else {
            staff = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(staff);
        em.flush();
        organisation.addStaff(staff);
        organisationRepository.saveAndFlush(organisation);
        Long staffId = staff.getId();

        // Get all the organisationList where staff equals to staffId
        defaultOrganisationShouldBeFound("staffId.equals=" + staffId);

        // Get all the organisationList where staff equals to (staffId + 1)
        defaultOrganisationShouldNotBeFound("staffId.equals=" + (staffId + 1));
    }

    @Test
    @Transactional
    void getAllOrganisationsByEmployeesIsEqualToSomething() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);
        Employee employees;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            employees = EmployeeResourceIT.createEntity(em);
            em.persist(employees);
            em.flush();
        } else {
            employees = TestUtil.findAll(em, Employee.class).get(0);
        }
        em.persist(employees);
        em.flush();
        organisation.addEmployees(employees);
        organisationRepository.saveAndFlush(organisation);
        Long employeesId = employees.getId();

        // Get all the organisationList where employees equals to employeesId
        defaultOrganisationShouldBeFound("employeesId.equals=" + employeesId);

        // Get all the organisationList where employees equals to (employeesId + 1)
        defaultOrganisationShouldNotBeFound("employeesId.equals=" + (employeesId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrganisationShouldBeFound(String filter) throws Exception {
        restOrganisationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organisation.getId().intValue())))
            .andExpect(jsonPath("$.[*].orgName").value(hasItem(DEFAULT_ORG_NAME)));

        // Check, that the count call also returns 1
        restOrganisationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrganisationShouldNotBeFound(String filter) throws Exception {
        restOrganisationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrganisationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrganisation() throws Exception {
        // Get the organisation
        restOrganisationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewOrganisation() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();

        // Update the organisation
        Organisation updatedOrganisation = organisationRepository.findById(organisation.getId()).get();
        // Disconnect from session so that the updates on updatedOrganisation are not directly saved in db
        em.detach(updatedOrganisation);
        updatedOrganisation.orgName(UPDATED_ORG_NAME);

        restOrganisationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOrganisation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOrganisation))
            )
            .andExpect(status().isOk());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        Organisation testOrganisation = organisationList.get(organisationList.size() - 1);
        assertThat(testOrganisation.getOrgName()).isEqualTo(UPDATED_ORG_NAME);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository).save(testOrganisation);
    }

    @Test
    @Transactional
    void putNonExistingOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();
        organisation.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrganisationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, organisation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(organisation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository, times(0)).save(organisation);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();
        organisation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganisationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(organisation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository, times(0)).save(organisation);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();
        organisation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganisationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(organisation)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository, times(0)).save(organisation);
    }

    @Test
    @Transactional
    void partialUpdateOrganisationWithPatch() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();

        // Update the organisation using partial update
        Organisation partialUpdatedOrganisation = new Organisation();
        partialUpdatedOrganisation.setId(organisation.getId());

        restOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrganisation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrganisation))
            )
            .andExpect(status().isOk());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        Organisation testOrganisation = organisationList.get(organisationList.size() - 1);
        assertThat(testOrganisation.getOrgName()).isEqualTo(DEFAULT_ORG_NAME);
    }

    @Test
    @Transactional
    void fullUpdateOrganisationWithPatch() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();

        // Update the organisation using partial update
        Organisation partialUpdatedOrganisation = new Organisation();
        partialUpdatedOrganisation.setId(organisation.getId());

        partialUpdatedOrganisation.orgName(UPDATED_ORG_NAME);

        restOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrganisation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrganisation))
            )
            .andExpect(status().isOk());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);
        Organisation testOrganisation = organisationList.get(organisationList.size() - 1);
        assertThat(testOrganisation.getOrgName()).isEqualTo(UPDATED_ORG_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();
        organisation.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, organisation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(organisation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository, times(0)).save(organisation);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();
        organisation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(organisation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository, times(0)).save(organisation);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrganisation() throws Exception {
        int databaseSizeBeforeUpdate = organisationRepository.findAll().size();
        organisation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrganisationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(organisation))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Organisation in the database
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository, times(0)).save(organisation);
    }

    @Test
    @Transactional
    void deleteOrganisation() throws Exception {
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);

        int databaseSizeBeforeDelete = organisationRepository.findAll().size();

        // Delete the organisation
        restOrganisationMockMvc
            .perform(delete(ENTITY_API_URL_ID, organisation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Organisation> organisationList = organisationRepository.findAll();
        assertThat(organisationList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Organisation in Elasticsearch
        verify(mockOrganisationSearchRepository, times(1)).deleteById(organisation.getId());
    }

    @Test
    @Transactional
    void searchOrganisation() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        organisationRepository.saveAndFlush(organisation);
        when(mockOrganisationSearchRepository.search("id:" + organisation.getId())).thenReturn(Stream.of(organisation));

        // Search the organisation
        restOrganisationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + organisation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organisation.getId().intValue())))
            .andExpect(jsonPath("$.[*].orgName").value(hasItem(DEFAULT_ORG_NAME)));
    }
}
