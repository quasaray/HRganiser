package com.quasaray.hrganiser.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.quasaray.hrganiser.IntegrationTest;
import com.quasaray.hrganiser.domain.Department;
import com.quasaray.hrganiser.domain.Employee;
import com.quasaray.hrganiser.domain.Sector;
import com.quasaray.hrganiser.repository.SectorRepository;
import com.quasaray.hrganiser.repository.search.SectorSearchRepository;
import com.quasaray.hrganiser.service.criteria.SectorCriteria;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SectorResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SectorResourceIT {

    private static final String DEFAULT_SECTOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SECTOR_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sectors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/sectors";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SectorRepository sectorRepository;

    /**
     * This repository is mocked in the com.quasaray.hrganiser.repository.search test package.
     *
     * @see com.quasaray.hrganiser.repository.search.SectorSearchRepositoryMockConfiguration
     */
    @Autowired
    private SectorSearchRepository mockSectorSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSectorMockMvc;

    private Sector sector;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sector createEntity(EntityManager em) {
        Sector sector = new Sector().sectorName(DEFAULT_SECTOR_NAME);
        return sector;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sector createUpdatedEntity(EntityManager em) {
        Sector sector = new Sector().sectorName(UPDATED_SECTOR_NAME);
        return sector;
    }

    @BeforeEach
    public void initTest() {
        sector = createEntity(em);
    }

    @Test
    @Transactional
    void createSector() throws Exception {
        int databaseSizeBeforeCreate = sectorRepository.findAll().size();
        // Create the Sector
        restSectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sector)))
            .andExpect(status().isCreated());

        // Validate the Sector in the database
        List<Sector> sectorList = sectorRepository.findAll();
        assertThat(sectorList).hasSize(databaseSizeBeforeCreate + 1);
        Sector testSector = sectorList.get(sectorList.size() - 1);
        assertThat(testSector.getSectorName()).isEqualTo(DEFAULT_SECTOR_NAME);

        // Validate the Sector in Elasticsearch
        verify(mockSectorSearchRepository, times(1)).save(testSector);
    }

    @Test
    @Transactional
    void createSectorWithExistingId() throws Exception {
        // Create the Sector with an existing ID
        sector.setId(1L);

        int databaseSizeBeforeCreate = sectorRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sector)))
            .andExpect(status().isBadRequest());

        // Validate the Sector in the database
        List<Sector> sectorList = sectorRepository.findAll();
        assertThat(sectorList).hasSize(databaseSizeBeforeCreate);

        // Validate the Sector in Elasticsearch
        verify(mockSectorSearchRepository, times(0)).save(sector);
    }

    @Test
    @Transactional
    void checkSectorNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = sectorRepository.findAll().size();
        // set the field null
        sector.setSectorName(null);

        // Create the Sector, which fails.

        restSectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sector)))
            .andExpect(status().isBadRequest());

        List<Sector> sectorList = sectorRepository.findAll();
        assertThat(sectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSectors() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);

        // Get all the sectorList
        restSectorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sector.getId().intValue())))
            .andExpect(jsonPath("$.[*].sectorName").value(hasItem(DEFAULT_SECTOR_NAME)));
    }

    @Test
    @Transactional
    void getSector() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);

        // Get the sector
        restSectorMockMvc
            .perform(get(ENTITY_API_URL_ID, sector.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sector.getId().intValue()))
            .andExpect(jsonPath("$.sectorName").value(DEFAULT_SECTOR_NAME));
    }

    @Test
    @Transactional
    void getSectorsByIdFiltering() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);

        Long id = sector.getId();

        defaultSectorShouldBeFound("id.equals=" + id);
        defaultSectorShouldNotBeFound("id.notEquals=" + id);

        defaultSectorShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSectorShouldNotBeFound("id.greaterThan=" + id);

        defaultSectorShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSectorShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSectorsBySectorNameIsEqualToSomething() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);

        // Get all the sectorList where sectorName equals to DEFAULT_SECTOR_NAME
        defaultSectorShouldBeFound("sectorName.equals=" + DEFAULT_SECTOR_NAME);

        // Get all the sectorList where sectorName equals to UPDATED_SECTOR_NAME
        defaultSectorShouldNotBeFound("sectorName.equals=" + UPDATED_SECTOR_NAME);
    }

    @Test
    @Transactional
    void getAllSectorsBySectorNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);

        // Get all the sectorList where sectorName not equals to DEFAULT_SECTOR_NAME
        defaultSectorShouldNotBeFound("sectorName.notEquals=" + DEFAULT_SECTOR_NAME);

        // Get all the sectorList where sectorName not equals to UPDATED_SECTOR_NAME
        defaultSectorShouldBeFound("sectorName.notEquals=" + UPDATED_SECTOR_NAME);
    }

    @Test
    @Transactional
    void getAllSectorsBySectorNameIsInShouldWork() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);

        // Get all the sectorList where sectorName in DEFAULT_SECTOR_NAME or UPDATED_SECTOR_NAME
        defaultSectorShouldBeFound("sectorName.in=" + DEFAULT_SECTOR_NAME + "," + UPDATED_SECTOR_NAME);

        // Get all the sectorList where sectorName equals to UPDATED_SECTOR_NAME
        defaultSectorShouldNotBeFound("sectorName.in=" + UPDATED_SECTOR_NAME);
    }

    @Test
    @Transactional
    void getAllSectorsBySectorNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);

        // Get all the sectorList where sectorName is not null
        defaultSectorShouldBeFound("sectorName.specified=true");

        // Get all the sectorList where sectorName is null
        defaultSectorShouldNotBeFound("sectorName.specified=false");
    }

    @Test
    @Transactional
    void getAllSectorsBySectorNameContainsSomething() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);

        // Get all the sectorList where sectorName contains DEFAULT_SECTOR_NAME
        defaultSectorShouldBeFound("sectorName.contains=" + DEFAULT_SECTOR_NAME);

        // Get all the sectorList where sectorName contains UPDATED_SECTOR_NAME
        defaultSectorShouldNotBeFound("sectorName.contains=" + UPDATED_SECTOR_NAME);
    }

    @Test
    @Transactional
    void getAllSectorsBySectorNameNotContainsSomething() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);

        // Get all the sectorList where sectorName does not contain DEFAULT_SECTOR_NAME
        defaultSectorShouldNotBeFound("sectorName.doesNotContain=" + DEFAULT_SECTOR_NAME);

        // Get all the sectorList where sectorName does not contain UPDATED_SECTOR_NAME
        defaultSectorShouldBeFound("sectorName.doesNotContain=" + UPDATED_SECTOR_NAME);
    }

    @Test
    @Transactional
    void getAllSectorsByEmployeesIsEqualToSomething() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);
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
        sector.addEmployees(employees);
        sectorRepository.saveAndFlush(sector);
        Long employeesId = employees.getId();

        // Get all the sectorList where employees equals to employeesId
        defaultSectorShouldBeFound("employeesId.equals=" + employeesId);

        // Get all the sectorList where employees equals to (employeesId + 1)
        defaultSectorShouldNotBeFound("employeesId.equals=" + (employeesId + 1));
    }

    @Test
    @Transactional
    void getAllSectorsByDepartmentsIsEqualToSomething() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);
        Department departments;
        if (TestUtil.findAll(em, Department.class).isEmpty()) {
            departments = DepartmentResourceIT.createEntity(em);
            em.persist(departments);
            em.flush();
        } else {
            departments = TestUtil.findAll(em, Department.class).get(0);
        }
        em.persist(departments);
        em.flush();
        sector.addDepartments(departments);
        sectorRepository.saveAndFlush(sector);
        Long departmentsId = departments.getId();

        // Get all the sectorList where departments equals to departmentsId
        defaultSectorShouldBeFound("departmentsId.equals=" + departmentsId);

        // Get all the sectorList where departments equals to (departmentsId + 1)
        defaultSectorShouldNotBeFound("departmentsId.equals=" + (departmentsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSectorShouldBeFound(String filter) throws Exception {
        restSectorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sector.getId().intValue())))
            .andExpect(jsonPath("$.[*].sectorName").value(hasItem(DEFAULT_SECTOR_NAME)));

        // Check, that the count call also returns 1
        restSectorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSectorShouldNotBeFound(String filter) throws Exception {
        restSectorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSectorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSector() throws Exception {
        // Get the sector
        restSectorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSector() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);

        int databaseSizeBeforeUpdate = sectorRepository.findAll().size();

        // Update the sector
        Sector updatedSector = sectorRepository.findById(sector.getId()).get();
        // Disconnect from session so that the updates on updatedSector are not directly saved in db
        em.detach(updatedSector);
        updatedSector.sectorName(UPDATED_SECTOR_NAME);

        restSectorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSector.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSector))
            )
            .andExpect(status().isOk());

        // Validate the Sector in the database
        List<Sector> sectorList = sectorRepository.findAll();
        assertThat(sectorList).hasSize(databaseSizeBeforeUpdate);
        Sector testSector = sectorList.get(sectorList.size() - 1);
        assertThat(testSector.getSectorName()).isEqualTo(UPDATED_SECTOR_NAME);

        // Validate the Sector in Elasticsearch
        verify(mockSectorSearchRepository).save(testSector);
    }

    @Test
    @Transactional
    void putNonExistingSector() throws Exception {
        int databaseSizeBeforeUpdate = sectorRepository.findAll().size();
        sector.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSectorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sector.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sector))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sector in the database
        List<Sector> sectorList = sectorRepository.findAll();
        assertThat(sectorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sector in Elasticsearch
        verify(mockSectorSearchRepository, times(0)).save(sector);
    }

    @Test
    @Transactional
    void putWithIdMismatchSector() throws Exception {
        int databaseSizeBeforeUpdate = sectorRepository.findAll().size();
        sector.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSectorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sector))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sector in the database
        List<Sector> sectorList = sectorRepository.findAll();
        assertThat(sectorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sector in Elasticsearch
        verify(mockSectorSearchRepository, times(0)).save(sector);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSector() throws Exception {
        int databaseSizeBeforeUpdate = sectorRepository.findAll().size();
        sector.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSectorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sector)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sector in the database
        List<Sector> sectorList = sectorRepository.findAll();
        assertThat(sectorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sector in Elasticsearch
        verify(mockSectorSearchRepository, times(0)).save(sector);
    }

    @Test
    @Transactional
    void partialUpdateSectorWithPatch() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);

        int databaseSizeBeforeUpdate = sectorRepository.findAll().size();

        // Update the sector using partial update
        Sector partialUpdatedSector = new Sector();
        partialUpdatedSector.setId(sector.getId());

        partialUpdatedSector.sectorName(UPDATED_SECTOR_NAME);

        restSectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSector.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSector))
            )
            .andExpect(status().isOk());

        // Validate the Sector in the database
        List<Sector> sectorList = sectorRepository.findAll();
        assertThat(sectorList).hasSize(databaseSizeBeforeUpdate);
        Sector testSector = sectorList.get(sectorList.size() - 1);
        assertThat(testSector.getSectorName()).isEqualTo(UPDATED_SECTOR_NAME);
    }

    @Test
    @Transactional
    void fullUpdateSectorWithPatch() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);

        int databaseSizeBeforeUpdate = sectorRepository.findAll().size();

        // Update the sector using partial update
        Sector partialUpdatedSector = new Sector();
        partialUpdatedSector.setId(sector.getId());

        partialUpdatedSector.sectorName(UPDATED_SECTOR_NAME);

        restSectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSector.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSector))
            )
            .andExpect(status().isOk());

        // Validate the Sector in the database
        List<Sector> sectorList = sectorRepository.findAll();
        assertThat(sectorList).hasSize(databaseSizeBeforeUpdate);
        Sector testSector = sectorList.get(sectorList.size() - 1);
        assertThat(testSector.getSectorName()).isEqualTo(UPDATED_SECTOR_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingSector() throws Exception {
        int databaseSizeBeforeUpdate = sectorRepository.findAll().size();
        sector.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sector.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sector))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sector in the database
        List<Sector> sectorList = sectorRepository.findAll();
        assertThat(sectorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sector in Elasticsearch
        verify(mockSectorSearchRepository, times(0)).save(sector);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSector() throws Exception {
        int databaseSizeBeforeUpdate = sectorRepository.findAll().size();
        sector.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sector))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sector in the database
        List<Sector> sectorList = sectorRepository.findAll();
        assertThat(sectorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sector in Elasticsearch
        verify(mockSectorSearchRepository, times(0)).save(sector);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSector() throws Exception {
        int databaseSizeBeforeUpdate = sectorRepository.findAll().size();
        sector.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSectorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(sector)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sector in the database
        List<Sector> sectorList = sectorRepository.findAll();
        assertThat(sectorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Sector in Elasticsearch
        verify(mockSectorSearchRepository, times(0)).save(sector);
    }

    @Test
    @Transactional
    void deleteSector() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);

        int databaseSizeBeforeDelete = sectorRepository.findAll().size();

        // Delete the sector
        restSectorMockMvc
            .perform(delete(ENTITY_API_URL_ID, sector.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Sector> sectorList = sectorRepository.findAll();
        assertThat(sectorList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Sector in Elasticsearch
        verify(mockSectorSearchRepository, times(1)).deleteById(sector.getId());
    }

    @Test
    @Transactional
    void searchSector() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        sectorRepository.saveAndFlush(sector);
        when(mockSectorSearchRepository.search("id:" + sector.getId())).thenReturn(Stream.of(sector));

        // Search the sector
        restSectorMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + sector.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sector.getId().intValue())))
            .andExpect(jsonPath("$.[*].sectorName").value(hasItem(DEFAULT_SECTOR_NAME)));
    }
}
