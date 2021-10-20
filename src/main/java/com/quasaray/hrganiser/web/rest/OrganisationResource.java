package com.quasaray.hrganiser.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.quasaray.hrganiser.domain.Organisation;
import com.quasaray.hrganiser.repository.OrganisationRepository;
import com.quasaray.hrganiser.service.OrganisationQueryService;
import com.quasaray.hrganiser.service.OrganisationService;
import com.quasaray.hrganiser.service.criteria.OrganisationCriteria;
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
 * REST controller for managing {@link com.quasaray.hrganiser.domain.Organisation}.
 */
@RestController
@RequestMapping("/api")
public class OrganisationResource {

    private final Logger log = LoggerFactory.getLogger(OrganisationResource.class);

    private static final String ENTITY_NAME = "organisation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrganisationService organisationService;

    private final OrganisationRepository organisationRepository;

    private final OrganisationQueryService organisationQueryService;

    public OrganisationResource(
        OrganisationService organisationService,
        OrganisationRepository organisationRepository,
        OrganisationQueryService organisationQueryService
    ) {
        this.organisationService = organisationService;
        this.organisationRepository = organisationRepository;
        this.organisationQueryService = organisationQueryService;
    }

    /**
     * {@code POST  /organisations} : Create a new organisation.
     *
     * @param organisation the organisation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new organisation, or with status {@code 400 (Bad Request)} if the organisation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/organisations")
    public ResponseEntity<Organisation> createOrganisation(@Valid @RequestBody Organisation organisation) throws URISyntaxException {
        log.debug("REST request to save Organisation : {}", organisation);
        if (organisation.getId() != null) {
            throw new BadRequestAlertException("A new organisation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Organisation result = organisationService.save(organisation);
        return ResponseEntity
            .created(new URI("/api/organisations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /organisations/:id} : Updates an existing organisation.
     *
     * @param id the id of the organisation to save.
     * @param organisation the organisation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organisation,
     * or with status {@code 400 (Bad Request)} if the organisation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the organisation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/organisations/{id}")
    public ResponseEntity<Organisation> updateOrganisation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Organisation organisation
    ) throws URISyntaxException {
        log.debug("REST request to update Organisation : {}, {}", id, organisation);
        if (organisation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, organisation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!organisationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Organisation result = organisationService.save(organisation);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, organisation.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /organisations/:id} : Partial updates given fields of an existing organisation, field will ignore if it is null
     *
     * @param id the id of the organisation to save.
     * @param organisation the organisation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated organisation,
     * or with status {@code 400 (Bad Request)} if the organisation is not valid,
     * or with status {@code 404 (Not Found)} if the organisation is not found,
     * or with status {@code 500 (Internal Server Error)} if the organisation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/organisations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Organisation> partialUpdateOrganisation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Organisation organisation
    ) throws URISyntaxException {
        log.debug("REST request to partial update Organisation partially : {}, {}", id, organisation);
        if (organisation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, organisation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!organisationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Organisation> result = organisationService.partialUpdate(organisation);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, organisation.getId().toString())
        );
    }

    /**
     * {@code GET  /organisations} : get all the organisations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of organisations in body.
     */
    @GetMapping("/organisations")
    public ResponseEntity<List<Organisation>> getAllOrganisations(OrganisationCriteria criteria) {
        log.debug("REST request to get Organisations by criteria: {}", criteria);
        List<Organisation> entityList = organisationQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /organisations/count} : count all the organisations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/organisations/count")
    public ResponseEntity<Long> countOrganisations(OrganisationCriteria criteria) {
        log.debug("REST request to count Organisations by criteria: {}", criteria);
        return ResponseEntity.ok().body(organisationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /organisations/:id} : get the "id" organisation.
     *
     * @param id the id of the organisation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the organisation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/organisations/{id}")
    public ResponseEntity<Organisation> getOrganisation(@PathVariable Long id) {
        log.debug("REST request to get Organisation : {}", id);
        Optional<Organisation> organisation = organisationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(organisation);
    }

    /**
     * {@code DELETE  /organisations/:id} : delete the "id" organisation.
     *
     * @param id the id of the organisation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/organisations/{id}")
    public ResponseEntity<Void> deleteOrganisation(@PathVariable Long id) {
        log.debug("REST request to delete Organisation : {}", id);
        organisationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/organisations?query=:query} : search for the organisation corresponding
     * to the query.
     *
     * @param query the query of the organisation search.
     * @return the result of the search.
     */
    @GetMapping("/_search/organisations")
    public List<Organisation> searchOrganisations(@RequestParam String query) {
        log.debug("REST request to search Organisations for query {}", query);
        return organisationService.search(query);
    }
}
