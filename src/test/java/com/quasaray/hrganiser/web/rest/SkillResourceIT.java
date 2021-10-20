package com.quasaray.hrganiser.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.quasaray.hrganiser.IntegrationTest;
import com.quasaray.hrganiser.domain.HasSkill;
import com.quasaray.hrganiser.domain.Skill;
import com.quasaray.hrganiser.repository.SkillRepository;
import com.quasaray.hrganiser.repository.search.SkillSearchRepository;
import com.quasaray.hrganiser.service.criteria.SkillCriteria;
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

/**
 * Integration tests for the {@link SkillResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SkillResourceIT {

    private static final String DEFAULT_SKILL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SKILL_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/skills";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/skills";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SkillRepository skillRepository;

    /**
     * This repository is mocked in the com.quasaray.hrganiser.repository.search test package.
     *
     * @see com.quasaray.hrganiser.repository.search.SkillSearchRepositoryMockConfiguration
     */
    @Autowired
    private SkillSearchRepository mockSkillSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSkillMockMvc;

    private Skill skill;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Skill createEntity(EntityManager em) {
        Skill skill = new Skill().skillName(DEFAULT_SKILL_NAME);
        return skill;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Skill createUpdatedEntity(EntityManager em) {
        Skill skill = new Skill().skillName(UPDATED_SKILL_NAME);
        return skill;
    }

    @BeforeEach
    public void initTest() {
        skill = createEntity(em);
    }

    @Test
    @Transactional
    void createSkill() throws Exception {
        int databaseSizeBeforeCreate = skillRepository.findAll().size();
        // Create the Skill
        restSkillMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(skill)))
            .andExpect(status().isCreated());

        // Validate the Skill in the database
        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeCreate + 1);
        Skill testSkill = skillList.get(skillList.size() - 1);
        assertThat(testSkill.getSkillName()).isEqualTo(DEFAULT_SKILL_NAME);

        // Validate the Skill in Elasticsearch
        verify(mockSkillSearchRepository, times(1)).save(testSkill);
    }

    @Test
    @Transactional
    void createSkillWithExistingId() throws Exception {
        // Create the Skill with an existing ID
        skill.setId(1L);

        int databaseSizeBeforeCreate = skillRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSkillMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(skill)))
            .andExpect(status().isBadRequest());

        // Validate the Skill in the database
        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeCreate);

        // Validate the Skill in Elasticsearch
        verify(mockSkillSearchRepository, times(0)).save(skill);
    }

    @Test
    @Transactional
    void checkSkillNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = skillRepository.findAll().size();
        // set the field null
        skill.setSkillName(null);

        // Create the Skill, which fails.

        restSkillMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(skill)))
            .andExpect(status().isBadRequest());

        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSkills() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        // Get all the skillList
        restSkillMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(skill.getId().intValue())))
            .andExpect(jsonPath("$.[*].skillName").value(hasItem(DEFAULT_SKILL_NAME)));
    }

    @Test
    @Transactional
    void getSkill() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        // Get the skill
        restSkillMockMvc
            .perform(get(ENTITY_API_URL_ID, skill.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(skill.getId().intValue()))
            .andExpect(jsonPath("$.skillName").value(DEFAULT_SKILL_NAME));
    }

    @Test
    @Transactional
    void getSkillsByIdFiltering() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        Long id = skill.getId();

        defaultSkillShouldBeFound("id.equals=" + id);
        defaultSkillShouldNotBeFound("id.notEquals=" + id);

        defaultSkillShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSkillShouldNotBeFound("id.greaterThan=" + id);

        defaultSkillShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSkillShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSkillsBySkillNameIsEqualToSomething() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        // Get all the skillList where skillName equals to DEFAULT_SKILL_NAME
        defaultSkillShouldBeFound("skillName.equals=" + DEFAULT_SKILL_NAME);

        // Get all the skillList where skillName equals to UPDATED_SKILL_NAME
        defaultSkillShouldNotBeFound("skillName.equals=" + UPDATED_SKILL_NAME);
    }

    @Test
    @Transactional
    void getAllSkillsBySkillNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        // Get all the skillList where skillName not equals to DEFAULT_SKILL_NAME
        defaultSkillShouldNotBeFound("skillName.notEquals=" + DEFAULT_SKILL_NAME);

        // Get all the skillList where skillName not equals to UPDATED_SKILL_NAME
        defaultSkillShouldBeFound("skillName.notEquals=" + UPDATED_SKILL_NAME);
    }

    @Test
    @Transactional
    void getAllSkillsBySkillNameIsInShouldWork() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        // Get all the skillList where skillName in DEFAULT_SKILL_NAME or UPDATED_SKILL_NAME
        defaultSkillShouldBeFound("skillName.in=" + DEFAULT_SKILL_NAME + "," + UPDATED_SKILL_NAME);

        // Get all the skillList where skillName equals to UPDATED_SKILL_NAME
        defaultSkillShouldNotBeFound("skillName.in=" + UPDATED_SKILL_NAME);
    }

    @Test
    @Transactional
    void getAllSkillsBySkillNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        // Get all the skillList where skillName is not null
        defaultSkillShouldBeFound("skillName.specified=true");

        // Get all the skillList where skillName is null
        defaultSkillShouldNotBeFound("skillName.specified=false");
    }

    @Test
    @Transactional
    void getAllSkillsBySkillNameContainsSomething() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        // Get all the skillList where skillName contains DEFAULT_SKILL_NAME
        defaultSkillShouldBeFound("skillName.contains=" + DEFAULT_SKILL_NAME);

        // Get all the skillList where skillName contains UPDATED_SKILL_NAME
        defaultSkillShouldNotBeFound("skillName.contains=" + UPDATED_SKILL_NAME);
    }

    @Test
    @Transactional
    void getAllSkillsBySkillNameNotContainsSomething() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        // Get all the skillList where skillName does not contain DEFAULT_SKILL_NAME
        defaultSkillShouldNotBeFound("skillName.doesNotContain=" + DEFAULT_SKILL_NAME);

        // Get all the skillList where skillName does not contain UPDATED_SKILL_NAME
        defaultSkillShouldBeFound("skillName.doesNotContain=" + UPDATED_SKILL_NAME);
    }

    @Test
    @Transactional
    void getAllSkillsByUsersIsEqualToSomething() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);
        HasSkill users;
        if (TestUtil.findAll(em, HasSkill.class).isEmpty()) {
            users = HasSkillResourceIT.createEntity(em);
            em.persist(users);
            em.flush();
        } else {
            users = TestUtil.findAll(em, HasSkill.class).get(0);
        }
        em.persist(users);
        em.flush();
        skill.addUsers(users);
        skillRepository.saveAndFlush(skill);
        Long usersId = users.getId();

        // Get all the skillList where users equals to usersId
        defaultSkillShouldBeFound("usersId.equals=" + usersId);

        // Get all the skillList where users equals to (usersId + 1)
        defaultSkillShouldNotBeFound("usersId.equals=" + (usersId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSkillShouldBeFound(String filter) throws Exception {
        restSkillMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(skill.getId().intValue())))
            .andExpect(jsonPath("$.[*].skillName").value(hasItem(DEFAULT_SKILL_NAME)));

        // Check, that the count call also returns 1
        restSkillMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSkillShouldNotBeFound(String filter) throws Exception {
        restSkillMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSkillMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSkill() throws Exception {
        // Get the skill
        restSkillMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSkill() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        int databaseSizeBeforeUpdate = skillRepository.findAll().size();

        // Update the skill
        Skill updatedSkill = skillRepository.findById(skill.getId()).get();
        // Disconnect from session so that the updates on updatedSkill are not directly saved in db
        em.detach(updatedSkill);
        updatedSkill.skillName(UPDATED_SKILL_NAME);

        restSkillMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSkill.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSkill))
            )
            .andExpect(status().isOk());

        // Validate the Skill in the database
        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeUpdate);
        Skill testSkill = skillList.get(skillList.size() - 1);
        assertThat(testSkill.getSkillName()).isEqualTo(UPDATED_SKILL_NAME);

        // Validate the Skill in Elasticsearch
        verify(mockSkillSearchRepository).save(testSkill);
    }

    @Test
    @Transactional
    void putNonExistingSkill() throws Exception {
        int databaseSizeBeforeUpdate = skillRepository.findAll().size();
        skill.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSkillMockMvc
            .perform(
                put(ENTITY_API_URL_ID, skill.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(skill))
            )
            .andExpect(status().isBadRequest());

        // Validate the Skill in the database
        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Skill in Elasticsearch
        verify(mockSkillSearchRepository, times(0)).save(skill);
    }

    @Test
    @Transactional
    void putWithIdMismatchSkill() throws Exception {
        int databaseSizeBeforeUpdate = skillRepository.findAll().size();
        skill.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSkillMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(skill))
            )
            .andExpect(status().isBadRequest());

        // Validate the Skill in the database
        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Skill in Elasticsearch
        verify(mockSkillSearchRepository, times(0)).save(skill);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSkill() throws Exception {
        int databaseSizeBeforeUpdate = skillRepository.findAll().size();
        skill.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSkillMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(skill)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Skill in the database
        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Skill in Elasticsearch
        verify(mockSkillSearchRepository, times(0)).save(skill);
    }

    @Test
    @Transactional
    void partialUpdateSkillWithPatch() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        int databaseSizeBeforeUpdate = skillRepository.findAll().size();

        // Update the skill using partial update
        Skill partialUpdatedSkill = new Skill();
        partialUpdatedSkill.setId(skill.getId());

        restSkillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSkill.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSkill))
            )
            .andExpect(status().isOk());

        // Validate the Skill in the database
        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeUpdate);
        Skill testSkill = skillList.get(skillList.size() - 1);
        assertThat(testSkill.getSkillName()).isEqualTo(DEFAULT_SKILL_NAME);
    }

    @Test
    @Transactional
    void fullUpdateSkillWithPatch() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        int databaseSizeBeforeUpdate = skillRepository.findAll().size();

        // Update the skill using partial update
        Skill partialUpdatedSkill = new Skill();
        partialUpdatedSkill.setId(skill.getId());

        partialUpdatedSkill.skillName(UPDATED_SKILL_NAME);

        restSkillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSkill.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSkill))
            )
            .andExpect(status().isOk());

        // Validate the Skill in the database
        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeUpdate);
        Skill testSkill = skillList.get(skillList.size() - 1);
        assertThat(testSkill.getSkillName()).isEqualTo(UPDATED_SKILL_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingSkill() throws Exception {
        int databaseSizeBeforeUpdate = skillRepository.findAll().size();
        skill.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSkillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, skill.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(skill))
            )
            .andExpect(status().isBadRequest());

        // Validate the Skill in the database
        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Skill in Elasticsearch
        verify(mockSkillSearchRepository, times(0)).save(skill);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSkill() throws Exception {
        int databaseSizeBeforeUpdate = skillRepository.findAll().size();
        skill.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSkillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(skill))
            )
            .andExpect(status().isBadRequest());

        // Validate the Skill in the database
        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Skill in Elasticsearch
        verify(mockSkillSearchRepository, times(0)).save(skill);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSkill() throws Exception {
        int databaseSizeBeforeUpdate = skillRepository.findAll().size();
        skill.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSkillMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(skill)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Skill in the database
        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Skill in Elasticsearch
        verify(mockSkillSearchRepository, times(0)).save(skill);
    }

    @Test
    @Transactional
    void deleteSkill() throws Exception {
        // Initialize the database
        skillRepository.saveAndFlush(skill);

        int databaseSizeBeforeDelete = skillRepository.findAll().size();

        // Delete the skill
        restSkillMockMvc
            .perform(delete(ENTITY_API_URL_ID, skill.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Skill> skillList = skillRepository.findAll();
        assertThat(skillList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Skill in Elasticsearch
        verify(mockSkillSearchRepository, times(1)).deleteById(skill.getId());
    }

    @Test
    @Transactional
    void searchSkill() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        skillRepository.saveAndFlush(skill);
        when(mockSkillSearchRepository.search("id:" + skill.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(skill), PageRequest.of(0, 1), 1));

        // Search the skill
        restSkillMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + skill.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(skill.getId().intValue())))
            .andExpect(jsonPath("$.[*].skillName").value(hasItem(DEFAULT_SKILL_NAME)));
    }
}
