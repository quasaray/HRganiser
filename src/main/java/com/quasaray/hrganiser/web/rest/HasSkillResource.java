package com.quasaray.hrganiser.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.quasaray.hrganiser.domain.HasSkill;
import com.quasaray.hrganiser.repository.HasSkillRepository;
import com.quasaray.hrganiser.service.HasSkillService;
import com.quasaray.hrganiser.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.quasaray.hrganiser.domain.HasSkill}.
 */
@RestController
@RequestMapping("/api")
public class HasSkillResource {

    private final Logger log = LoggerFactory.getLogger(HasSkillResource.class);

    private static final String ENTITY_NAME = "hasSkill";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HasSkillService hasSkillService;

    private final HasSkillRepository hasSkillRepository;

    public HasSkillResource(HasSkillService hasSkillService, HasSkillRepository hasSkillRepository) {
        this.hasSkillService = hasSkillService;
        this.hasSkillRepository = hasSkillRepository;
    }

    /**
     * {@code POST  /has-skills} : Create a new hasSkill.
     *
     * @param hasSkill the hasSkill to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new hasSkill, or with status {@code 400 (Bad Request)} if the hasSkill has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/has-skills")
    public ResponseEntity<HasSkill> createHasSkill(@Valid @RequestBody HasSkill hasSkill) throws URISyntaxException {
        log.debug("REST request to save HasSkill : {}", hasSkill);
        if (hasSkill.getId() != null) {
            throw new BadRequestAlertException("A new hasSkill cannot already have an ID", ENTITY_NAME, "idexists");
        }
        HasSkill result = hasSkillService.save(hasSkill);
        return ResponseEntity
            .created(new URI("/api/has-skills/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /has-skills/:id} : Updates an existing hasSkill.
     *
     * @param id the id of the hasSkill to save.
     * @param hasSkill the hasSkill to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hasSkill,
     * or with status {@code 400 (Bad Request)} if the hasSkill is not valid,
     * or with status {@code 500 (Internal Server Error)} if the hasSkill couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/has-skills/{id}")
    public ResponseEntity<HasSkill> updateHasSkill(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody HasSkill hasSkill
    ) throws URISyntaxException {
        log.debug("REST request to update HasSkill : {}, {}", id, hasSkill);
        if (hasSkill.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hasSkill.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!hasSkillRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        HasSkill result = hasSkillService.save(hasSkill);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, hasSkill.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /has-skills/:id} : Partial updates given fields of an existing hasSkill, field will ignore if it is null
     *
     * @param id the id of the hasSkill to save.
     * @param hasSkill the hasSkill to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hasSkill,
     * or with status {@code 400 (Bad Request)} if the hasSkill is not valid,
     * or with status {@code 404 (Not Found)} if the hasSkill is not found,
     * or with status {@code 500 (Internal Server Error)} if the hasSkill couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/has-skills/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HasSkill> partialUpdateHasSkill(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody HasSkill hasSkill
    ) throws URISyntaxException {
        log.debug("REST request to partial update HasSkill partially : {}, {}", id, hasSkill);
        if (hasSkill.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hasSkill.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!hasSkillRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HasSkill> result = hasSkillService.partialUpdate(hasSkill);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, hasSkill.getId().toString())
        );
    }

    /**
     * {@code GET  /has-skills} : get all the hasSkills.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of hasSkills in body.
     */
    @GetMapping("/has-skills")
    public List<HasSkill> getAllHasSkills() {
        log.debug("REST request to get all HasSkills");
        return hasSkillService.findAll();
    }

    /**
     * {@code GET  /has-skills/:id} : get the "id" hasSkill.
     *
     * @param id the id of the hasSkill to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the hasSkill, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/has-skills/{id}")
    public ResponseEntity<HasSkill> getHasSkill(@PathVariable Long id) {
        log.debug("REST request to get HasSkill : {}", id);
        Optional<HasSkill> hasSkill = hasSkillService.findOne(id);
        return ResponseUtil.wrapOrNotFound(hasSkill);
    }

    /**
     * {@code DELETE  /has-skills/:id} : delete the "id" hasSkill.
     *
     * @param id the id of the hasSkill to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/has-skills/{id}")
    public ResponseEntity<Void> deleteHasSkill(@PathVariable Long id) {
        log.debug("REST request to delete HasSkill : {}", id);
        hasSkillService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/has-skills?query=:query} : search for the hasSkill corresponding
     * to the query.
     *
     * @param query the query of the hasSkill search.
     * @return the result of the search.
     */
    @GetMapping("/_search/has-skills")
    public List<HasSkill> searchHasSkills(@RequestParam String query) {
        log.debug("REST request to search HasSkills for query {}", query);
        return hasSkillService.search(query);
    }
}
