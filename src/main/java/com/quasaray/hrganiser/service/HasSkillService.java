package com.quasaray.hrganiser.service;

import com.quasaray.hrganiser.domain.HasSkill;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link HasSkill}.
 */
public interface HasSkillService {
    /**
     * Save a hasSkill.
     *
     * @param hasSkill the entity to save.
     * @return the persisted entity.
     */
    HasSkill save(HasSkill hasSkill);

    /**
     * Partially updates a hasSkill.
     *
     * @param hasSkill the entity to update partially.
     * @return the persisted entity.
     */
    Optional<HasSkill> partialUpdate(HasSkill hasSkill);

    /**
     * Get all the hasSkills.
     *
     * @return the list of entities.
     */
    List<HasSkill> findAll();

    /**
     * Get the "id" hasSkill.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<HasSkill> findOne(Long id);

    /**
     * Delete the "id" hasSkill.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the hasSkill corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<HasSkill> search(String query);
}
