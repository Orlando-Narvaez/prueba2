package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Adulto;
import com.mycompany.myapp.repository.AdultoRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Adulto}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AdultoResource {

    private final Logger log = LoggerFactory.getLogger(AdultoResource.class);

    private static final String ENTITY_NAME = "adulto";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AdultoRepository adultoRepository;

    public AdultoResource(AdultoRepository adultoRepository) {
        this.adultoRepository = adultoRepository;
    }

    /**
     * {@code POST  /adultos} : Create a new adulto.
     *
     * @param adulto the adulto to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new adulto, or with status {@code 400 (Bad Request)} if the adulto has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/adultos")
    public ResponseEntity<Adulto> createAdulto(@Valid @RequestBody Adulto adulto) throws URISyntaxException {
        log.debug("REST request to save Adulto : {}", adulto);
        if (adulto.getId() != null) {
            throw new BadRequestAlertException("A new adulto cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Adulto result = adultoRepository.save(adulto);
        return ResponseEntity
            .created(new URI("/api/adultos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /adultos/:id} : Updates an existing adulto.
     *
     * @param id the id of the adulto to save.
     * @param adulto the adulto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated adulto,
     * or with status {@code 400 (Bad Request)} if the adulto is not valid,
     * or with status {@code 500 (Internal Server Error)} if the adulto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/adultos/{id}")
    public ResponseEntity<Adulto> updateAdulto(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Adulto adulto
    ) throws URISyntaxException {
        log.debug("REST request to update Adulto : {}, {}", id, adulto);
        if (adulto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, adulto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!adultoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Adulto result = adultoRepository.save(adulto);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, adulto.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /adultos/:id} : Partial updates given fields of an existing adulto, field will ignore if it is null
     *
     * @param id the id of the adulto to save.
     * @param adulto the adulto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated adulto,
     * or with status {@code 400 (Bad Request)} if the adulto is not valid,
     * or with status {@code 404 (Not Found)} if the adulto is not found,
     * or with status {@code 500 (Internal Server Error)} if the adulto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/adultos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Adulto> partialUpdateAdulto(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Adulto adulto
    ) throws URISyntaxException {
        log.debug("REST request to partial update Adulto partially : {}, {}", id, adulto);
        if (adulto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, adulto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!adultoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Adulto> result = adultoRepository
            .findById(adulto.getId())
            .map(existingAdulto -> {
                if (adulto.getNombre() != null) {
                    existingAdulto.setNombre(adulto.getNombre());
                }
                if (adulto.getApellido() != null) {
                    existingAdulto.setApellido(adulto.getApellido());
                }
                if (adulto.getEdad() != null) {
                    existingAdulto.setEdad(adulto.getEdad());
                }
                if (adulto.getFechaNacimiento() != null) {
                    existingAdulto.setFechaNacimiento(adulto.getFechaNacimiento());
                }

                return existingAdulto;
            })
            .map(adultoRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, adulto.getId().toString())
        );
    }

    /**
     * {@code GET  /adultos} : get all the adultos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of adultos in body.
     */
    @GetMapping("/adultos")
    public List<Adulto> getAllAdultos() {
        log.debug("REST request to get all Adultos");
        return adultoRepository.findAll();
    }

    /**
     * {@code GET  /adultos/:id} : get the "id" adulto.
     *
     * @param id the id of the adulto to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the adulto, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/adultos/{id}")
    public ResponseEntity<Adulto> getAdulto(@PathVariable Long id) {
        log.debug("REST request to get Adulto : {}", id);
        Optional<Adulto> adulto = adultoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(adulto);
    }

    /**
     * {@code DELETE  /adultos/:id} : delete the "id" adulto.
     *
     * @param id the id of the adulto to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/adultos/{id}")
    public ResponseEntity<Void> deleteAdulto(@PathVariable Long id) {
        log.debug("REST request to delete Adulto : {}", id);
        adultoRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
