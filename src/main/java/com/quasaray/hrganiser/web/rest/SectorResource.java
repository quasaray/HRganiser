package com.quasaray.hrganiser.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.quasaray.hrganiser.domain.Sector;
import com.quasaray.hrganiser.repository.SectorRepository;
import com.quasaray.hrganiser.service.SectorQueryService;
import com.quasaray.hrganiser.service.SectorService;
import com.quasaray.hrganiser.service.criteria.SectorCriteria;
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
 * REST controller for managing {@link com.quasaray.hrganiser.domain.Sector}.
 */
@RestController
@RequestMapping("/api")
public class SectorResource {

    private final Logger log = LoggerFactory.getLogger(SectorResource.class);

    private static final String ENTITY_NAME = "sector";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SectorService sectorService;

    private final SectorRepository sectorRepository;

    private final SectorQueryService sectorQueryService;

    public SectorResource(SectorService sectorService, SectorRepository sectorRepository, SectorQueryService sectorQueryService) {
        this.sectorService = sectorService;
        this.sectorRepository = sectorRepository;
        this.sectorQueryService = sectorQueryService;
    }

    /**
     * {@code POST  /sectors} : Create a new sector.
     *
     * @param sector the sector to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sector, or with status {@code 400 (Bad Request)} if the sector has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sectors")
    public ResponseEntity<Sector> createSector(@Valid @RequestBody Sector sector) throws URISyntaxException {
        log.debug("REST request to save Sector : {}", sector);
        if (sector.getId() != null) {
            throw new BadRequestAlertException("A new sector cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Sector result = sectorService.save(sector);
        return ResponseEntity
            .created(new URI("/api/sectors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sectors/:id} : Updates an existing sector.
     *
     * @param id the id of the sector to save.
     * @param sector the sector to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sector,
     * or with status {@code 400 (Bad Request)} if the sector is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sector couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sectors/{id}")
    public ResponseEntity<Sector> updateSector(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Sector sector
    ) throws URISyntaxException {
        log.debug("REST request to update Sector : {}, {}", id, sector);
        if (sector.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sector.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sectorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Sector result = sectorService.save(sector);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sector.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sectors/:id} : Partial updates given fields of an existing sector, field will ignore if it is null
     *
     * @param id the id of the sector to save.
     * @param sector the sector to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sector,
     * or with status {@code 400 (Bad Request)} if the sector is not valid,
     * or with status {@code 404 (Not Found)} if the sector is not found,
     * or with status {@code 500 (Internal Server Error)} if the sector couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/sectors/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Sector> partialUpdateSector(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Sector sector
    ) throws URISyntaxException {
        log.debug("REST request to partial update Sector partially : {}, {}", id, sector);
        if (sector.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sector.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sectorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Sector> result = sectorService.partialUpdate(sector);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sector.getId().toString())
        );
    }

    /**
     * {@code GET  /sectors} : get all the sectors.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sectors in body.
     */
    @GetMapping("/sectors")
    public ResponseEntity<List<Sector>> getAllSectors(SectorCriteria criteria) {
        log.debug("REST request to get Sectors by criteria: {}", criteria);
        List<Sector> entityList = sectorQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /sectors/count} : count all the sectors.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/sectors/count")
    public ResponseEntity<Long> countSectors(SectorCriteria criteria) {
        log.debug("REST request to count Sectors by criteria: {}", criteria);
        return ResponseEntity.ok().body(sectorQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sectors/:id} : get the "id" sector.
     *
     * @param id the id of the sector to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sector, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sectors/{id}")
    public ResponseEntity<Sector> getSector(@PathVariable Long id) {
        log.debug("REST request to get Sector : {}", id);
        Optional<Sector> sector = sectorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sector);
    }

    /**
     * {@code DELETE  /sectors/:id} : delete the "id" sector.
     *
     * @param id the id of the sector to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sectors/{id}")
    public ResponseEntity<Void> deleteSector(@PathVariable Long id) {
        log.debug("REST request to delete Sector : {}", id);
        sectorService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/sectors?query=:query} : search for the sector corresponding
     * to the query.
     *
     * @param query the query of the sector search.
     * @return the result of the search.
     */
    @GetMapping("/_search/sectors")
    public List<Sector> searchSectors(@RequestParam String query) {
        log.debug("REST request to search Sectors for query {}", query);
        return sectorService.search(query);
    }
}
