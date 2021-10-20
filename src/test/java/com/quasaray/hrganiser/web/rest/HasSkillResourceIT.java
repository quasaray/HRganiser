package com.quasaray.hrganiser.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.quasaray.hrganiser.IntegrationTest;
import com.quasaray.hrganiser.domain.Employee;
import com.quasaray.hrganiser.domain.HasSkill;
import com.quasaray.hrganiser.domain.Skill;
import com.quasaray.hrganiser.domain.enumeration.SkillLevel;
import com.quasaray.hrganiser.repository.HasSkillRepository;
import com.quasaray.hrganiser.repository.search.HasSkillSearchRepository;
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
 * Integration tests for the {@link HasSkillResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class HasSkillResourceIT {

    private static final SkillLevel DEFAULT_LEVEL = SkillLevel.Basic;
    private static final SkillLevel UPDATED_LEVEL = SkillLevel.Intermediate;

    private static final String ENTITY_API_URL = "/api/has-skills";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/has-skills";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HasSkillRepository hasSkillRepository;

    /**
     * This repository is mocked in the com.quasaray.hrganiser.repository.search test package.
     *
     * @see com.quasaray.hrganiser.repository.search.HasSkillSearchRepositoryMockConfiguration
     */
    @Autowired
    private HasSkillSearchRepository mockHasSkillSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHasSkillMockMvc;

    private HasSkill hasSkill;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HasSkill createEntity(EntityManager em) {
        HasSkill hasSkill = new HasSkill().level(DEFAULT_LEVEL);
        // Add required entity
        Skill skill;
        if (TestUtil.findAll(em, Skill.class).isEmpty()) {
            skill = SkillResourceIT.createEntity(em);
            em.persist(skill);
            em.flush();
        } else {
            skill = TestUtil.findAll(em, Skill.class).get(0);
        }
        hasSkill.setSkill(skill);
        // Add required entity
        Employee employee;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            employee = EmployeeResourceIT.createEntity(em);
            em.persist(employee);
            em.flush();
        } else {
            employee = TestUtil.findAll(em, Employee.class).get(0);
        }
        hasSkill.setEmployee(employee);
        return hasSkill;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HasSkill createUpdatedEntity(EntityManager em) {
        HasSkill hasSkill = new HasSkill().level(UPDATED_LEVEL);
        // Add required entity
        Skill skill;
        if (TestUtil.findAll(em, Skill.class).isEmpty()) {
            skill = SkillResourceIT.createUpdatedEntity(em);
            em.persist(skill);
            em.flush();
        } else {
            skill = TestUtil.findAll(em, Skill.class).get(0);
        }
        hasSkill.setSkill(skill);
        // Add required entity
        Employee employee;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            employee = EmployeeResourceIT.createUpdatedEntity(em);
            em.persist(employee);
            em.flush();
        } else {
            employee = TestUtil.findAll(em, Employee.class).get(0);
        }
        hasSkill.setEmployee(employee);
        return hasSkill;
    }

    @BeforeEach
    public void initTest() {
        hasSkill = createEntity(em);
    }

    @Test
    @Transactional
    void createHasSkill() throws Exception {
        int databaseSizeBeforeCreate = hasSkillRepository.findAll().size();
        // Create the HasSkill
        restHasSkillMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hasSkill)))
            .andExpect(status().isCreated());

        // Validate the HasSkill in the database
        List<HasSkill> hasSkillList = hasSkillRepository.findAll();
        assertThat(hasSkillList).hasSize(databaseSizeBeforeCreate + 1);
        HasSkill testHasSkill = hasSkillList.get(hasSkillList.size() - 1);
        assertThat(testHasSkill.getLevel()).isEqualTo(DEFAULT_LEVEL);

        // Validate the HasSkill in Elasticsearch
        verify(mockHasSkillSearchRepository, times(1)).save(testHasSkill);
    }

    @Test
    @Transactional
    void createHasSkillWithExistingId() throws Exception {
        // Create the HasSkill with an existing ID
        hasSkill.setId(1L);

        int databaseSizeBeforeCreate = hasSkillRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHasSkillMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hasSkill)))
            .andExpect(status().isBadRequest());

        // Validate the HasSkill in the database
        List<HasSkill> hasSkillList = hasSkillRepository.findAll();
        assertThat(hasSkillList).hasSize(databaseSizeBeforeCreate);

        // Validate the HasSkill in Elasticsearch
        verify(mockHasSkillSearchRepository, times(0)).save(hasSkill);
    }

    @Test
    @Transactional
    void checkLevelIsRequired() throws Exception {
        int databaseSizeBeforeTest = hasSkillRepository.findAll().size();
        // set the field null
        hasSkill.setLevel(null);

        // Create the HasSkill, which fails.

        restHasSkillMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hasSkill)))
            .andExpect(status().isBadRequest());

        List<HasSkill> hasSkillList = hasSkillRepository.findAll();
        assertThat(hasSkillList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllHasSkills() throws Exception {
        // Initialize the database
        hasSkillRepository.saveAndFlush(hasSkill);

        // Get all the hasSkillList
        restHasSkillMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hasSkill.getId().intValue())))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL.toString())));
    }

    @Test
    @Transactional
    void getHasSkill() throws Exception {
        // Initialize the database
        hasSkillRepository.saveAndFlush(hasSkill);

        // Get the hasSkill
        restHasSkillMockMvc
            .perform(get(ENTITY_API_URL_ID, hasSkill.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(hasSkill.getId().intValue()))
            .andExpect(jsonPath("$.level").value(DEFAULT_LEVEL.toString()));
    }

    @Test
    @Transactional
    void getNonExistingHasSkill() throws Exception {
        // Get the hasSkill
        restHasSkillMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewHasSkill() throws Exception {
        // Initialize the database
        hasSkillRepository.saveAndFlush(hasSkill);

        int databaseSizeBeforeUpdate = hasSkillRepository.findAll().size();

        // Update the hasSkill
        HasSkill updatedHasSkill = hasSkillRepository.findById(hasSkill.getId()).get();
        // Disconnect from session so that the updates on updatedHasSkill are not directly saved in db
        em.detach(updatedHasSkill);
        updatedHasSkill.level(UPDATED_LEVEL);

        restHasSkillMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedHasSkill.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedHasSkill))
            )
            .andExpect(status().isOk());

        // Validate the HasSkill in the database
        List<HasSkill> hasSkillList = hasSkillRepository.findAll();
        assertThat(hasSkillList).hasSize(databaseSizeBeforeUpdate);
        HasSkill testHasSkill = hasSkillList.get(hasSkillList.size() - 1);
        assertThat(testHasSkill.getLevel()).isEqualTo(UPDATED_LEVEL);

        // Validate the HasSkill in Elasticsearch
        verify(mockHasSkillSearchRepository).save(testHasSkill);
    }

    @Test
    @Transactional
    void putNonExistingHasSkill() throws Exception {
        int databaseSizeBeforeUpdate = hasSkillRepository.findAll().size();
        hasSkill.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHasSkillMockMvc
            .perform(
                put(ENTITY_API_URL_ID, hasSkill.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(hasSkill))
            )
            .andExpect(status().isBadRequest());

        // Validate the HasSkill in the database
        List<HasSkill> hasSkillList = hasSkillRepository.findAll();
        assertThat(hasSkillList).hasSize(databaseSizeBeforeUpdate);

        // Validate the HasSkill in Elasticsearch
        verify(mockHasSkillSearchRepository, times(0)).save(hasSkill);
    }

    @Test
    @Transactional
    void putWithIdMismatchHasSkill() throws Exception {
        int databaseSizeBeforeUpdate = hasSkillRepository.findAll().size();
        hasSkill.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHasSkillMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(hasSkill))
            )
            .andExpect(status().isBadRequest());

        // Validate the HasSkill in the database
        List<HasSkill> hasSkillList = hasSkillRepository.findAll();
        assertThat(hasSkillList).hasSize(databaseSizeBeforeUpdate);

        // Validate the HasSkill in Elasticsearch
        verify(mockHasSkillSearchRepository, times(0)).save(hasSkill);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHasSkill() throws Exception {
        int databaseSizeBeforeUpdate = hasSkillRepository.findAll().size();
        hasSkill.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHasSkillMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hasSkill)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HasSkill in the database
        List<HasSkill> hasSkillList = hasSkillRepository.findAll();
        assertThat(hasSkillList).hasSize(databaseSizeBeforeUpdate);

        // Validate the HasSkill in Elasticsearch
        verify(mockHasSkillSearchRepository, times(0)).save(hasSkill);
    }

    @Test
    @Transactional
    void partialUpdateHasSkillWithPatch() throws Exception {
        // Initialize the database
        hasSkillRepository.saveAndFlush(hasSkill);

        int databaseSizeBeforeUpdate = hasSkillRepository.findAll().size();

        // Update the hasSkill using partial update
        HasSkill partialUpdatedHasSkill = new HasSkill();
        partialUpdatedHasSkill.setId(hasSkill.getId());

        partialUpdatedHasSkill.level(UPDATED_LEVEL);

        restHasSkillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHasSkill.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHasSkill))
            )
            .andExpect(status().isOk());

        // Validate the HasSkill in the database
        List<HasSkill> hasSkillList = hasSkillRepository.findAll();
        assertThat(hasSkillList).hasSize(databaseSizeBeforeUpdate);
        HasSkill testHasSkill = hasSkillList.get(hasSkillList.size() - 1);
        assertThat(testHasSkill.getLevel()).isEqualTo(UPDATED_LEVEL);
    }

    @Test
    @Transactional
    void fullUpdateHasSkillWithPatch() throws Exception {
        // Initialize the database
        hasSkillRepository.saveAndFlush(hasSkill);

        int databaseSizeBeforeUpdate = hasSkillRepository.findAll().size();

        // Update the hasSkill using partial update
        HasSkill partialUpdatedHasSkill = new HasSkill();
        partialUpdatedHasSkill.setId(hasSkill.getId());

        partialUpdatedHasSkill.level(UPDATED_LEVEL);

        restHasSkillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHasSkill.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHasSkill))
            )
            .andExpect(status().isOk());

        // Validate the HasSkill in the database
        List<HasSkill> hasSkillList = hasSkillRepository.findAll();
        assertThat(hasSkillList).hasSize(databaseSizeBeforeUpdate);
        HasSkill testHasSkill = hasSkillList.get(hasSkillList.size() - 1);
        assertThat(testHasSkill.getLevel()).isEqualTo(UPDATED_LEVEL);
    }

    @Test
    @Transactional
    void patchNonExistingHasSkill() throws Exception {
        int databaseSizeBeforeUpdate = hasSkillRepository.findAll().size();
        hasSkill.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHasSkillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, hasSkill.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(hasSkill))
            )
            .andExpect(status().isBadRequest());

        // Validate the HasSkill in the database
        List<HasSkill> hasSkillList = hasSkillRepository.findAll();
        assertThat(hasSkillList).hasSize(databaseSizeBeforeUpdate);

        // Validate the HasSkill in Elasticsearch
        verify(mockHasSkillSearchRepository, times(0)).save(hasSkill);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHasSkill() throws Exception {
        int databaseSizeBeforeUpdate = hasSkillRepository.findAll().size();
        hasSkill.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHasSkillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(hasSkill))
            )
            .andExpect(status().isBadRequest());

        // Validate the HasSkill in the database
        List<HasSkill> hasSkillList = hasSkillRepository.findAll();
        assertThat(hasSkillList).hasSize(databaseSizeBeforeUpdate);

        // Validate the HasSkill in Elasticsearch
        verify(mockHasSkillSearchRepository, times(0)).save(hasSkill);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHasSkill() throws Exception {
        int databaseSizeBeforeUpdate = hasSkillRepository.findAll().size();
        hasSkill.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHasSkillMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(hasSkill)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HasSkill in the database
        List<HasSkill> hasSkillList = hasSkillRepository.findAll();
        assertThat(hasSkillList).hasSize(databaseSizeBeforeUpdate);

        // Validate the HasSkill in Elasticsearch
        verify(mockHasSkillSearchRepository, times(0)).save(hasSkill);
    }

    @Test
    @Transactional
    void deleteHasSkill() throws Exception {
        // Initialize the database
        hasSkillRepository.saveAndFlush(hasSkill);

        int databaseSizeBeforeDelete = hasSkillRepository.findAll().size();

        // Delete the hasSkill
        restHasSkillMockMvc
            .perform(delete(ENTITY_API_URL_ID, hasSkill.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<HasSkill> hasSkillList = hasSkillRepository.findAll();
        assertThat(hasSkillList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the HasSkill in Elasticsearch
        verify(mockHasSkillSearchRepository, times(1)).deleteById(hasSkill.getId());
    }

    @Test
    @Transactional
    void searchHasSkill() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        hasSkillRepository.saveAndFlush(hasSkill);
        when(mockHasSkillSearchRepository.search("id:" + hasSkill.getId())).thenReturn(Stream.of(hasSkill));

        // Search the hasSkill
        restHasSkillMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + hasSkill.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hasSkill.getId().intValue())))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL.toString())));
    }
}
