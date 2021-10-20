package com.quasaray.hrganiser.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.quasaray.hrganiser.domain.Skill;
import com.quasaray.hrganiser.repository.SkillRepository;
import com.quasaray.hrganiser.repository.search.SkillSearchRepository;
import com.quasaray.hrganiser.service.SkillService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Skill}.
 */
@Service
@Transactional
public class SkillServiceImpl implements SkillService {

    private final Logger log = LoggerFactory.getLogger(SkillServiceImpl.class);

    private final SkillRepository skillRepository;

    private final SkillSearchRepository skillSearchRepository;

    public SkillServiceImpl(SkillRepository skillRepository, SkillSearchRepository skillSearchRepository) {
        this.skillRepository = skillRepository;
        this.skillSearchRepository = skillSearchRepository;
    }

    @Override
    public Skill save(Skill skill) {
        log.debug("Request to save Skill : {}", skill);
        Skill result = skillRepository.save(skill);
        skillSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<Skill> partialUpdate(Skill skill) {
        log.debug("Request to partially update Skill : {}", skill);

        return skillRepository
            .findById(skill.getId())
            .map(existingSkill -> {
                if (skill.getSkillName() != null) {
                    existingSkill.setSkillName(skill.getSkillName());
                }

                return existingSkill;
            })
            .map(skillRepository::save)
            .map(savedSkill -> {
                skillSearchRepository.save(savedSkill);

                return savedSkill;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Skill> findAll(Pageable pageable) {
        log.debug("Request to get all Skills");
        return skillRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Skill> findOne(Long id) {
        log.debug("Request to get Skill : {}", id);
        return skillRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Skill : {}", id);
        skillRepository.deleteById(id);
        skillSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Skill> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Skills for query {}", query);
        return skillSearchRepository.search(query, pageable);
    }
}
