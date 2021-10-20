package com.quasaray.hrganiser.repository;

import com.quasaray.hrganiser.domain.HasSkill;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the HasSkill entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HasSkillRepository extends JpaRepository<HasSkill, Long> {}
