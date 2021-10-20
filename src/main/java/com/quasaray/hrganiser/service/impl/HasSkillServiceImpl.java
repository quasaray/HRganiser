package com.quasaray.hrganiser.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.quasaray.hrganiser.domain.HasSkill;
import com.quasaray.hrganiser.repository.HasSkillRepository;
import com.quasaray.hrganiser.repository.search.HasSkillSearchRepository;
import com.quasaray.hrganiser.service.HasSkillService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link HasSkill}.
 */
@Service
@Transactional
public class HasSkillServiceImpl implements HasSkillService {

    private final Logger log = LoggerFactory.getLogger(HasSkillServiceImpl.class);

    private final HasSkillRepository hasSkillRepository;

    private final HasSkillSearchRepository hasSkillSearchRepository;

    public HasSkillServiceImpl(HasSkillRepository hasSkillRepository, HasSkillSearchRepository hasSkillSearchRepository) {
        this.hasSkillRepository = hasSkillRepository;
        this.hasSkillSearchRepository = hasSkillSearchRepository;
    }

    @Override
    public HasSkill save(HasSkill hasSkill) {
        log.debug("Request to save HasSkill : {}", hasSkill);
        HasSkill result = hasSkillRepository.save(hasSkill);
        hasSkillSearchRepository.save(result);
        return result;
    }

    @Override
    public Optional<HasSkill> partialUpdate(HasSkill hasSkill) {
        log.debug("Request to partially update HasSkill : {}", hasSkill);

        return hasSkillRepository
            .findById(hasSkill.getId())
            .map(existingHasSkill -> {
                if (hasSkill.getLevel() != null) {
                    existingHasSkill.setLevel(hasSkill.getLevel());
                }

                return existingHasSkill;
            })
            .map(hasSkillRepository::save)
            .map(savedHasSkill -> {
                hasSkillSearchRepository.save(savedHasSkill);

                return savedHasSkill;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<HasSkill> findAll() {
        log.debug("Request to get all HasSkills");
        return hasSkillRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HasSkill> findOne(Long id) {
        log.debug("Request to get HasSkill : {}", id);
        return hasSkillRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete HasSkill : {}", id);
        hasSkillRepository.deleteById(id);
        hasSkillSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HasSkill> search(String query) {
        log.debug("Request to search HasSkills for query {}", query);
        return StreamSupport.stream(hasSkillSearchRepository.search(query).spliterator(), false).collect(Collectors.toList());
    }
}
