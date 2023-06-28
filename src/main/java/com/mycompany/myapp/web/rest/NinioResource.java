package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Ninio;
import com.mycompany.myapp.repository.NinioRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Ninio}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class NinioResource {

    private final Logger log = LoggerFactory.getLogger(NinioResource.class);

    private static final String ENTITY_NAME = "ninio";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NinioRepository ninioRepository;

    public NinioResource(NinioRepository ninioRepository) {
        this.ninioRepository = ninioRepository;
    }

    /**
     * {@code POST  /ninios} : Create a new ninio.
     *
     * @param ninio the ninio to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ninio, or with status {@code 400 (Bad Request)} if the ninio has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/ninios")
    public ResponseEntity<Ninio> createNinio(@Valid @RequestBody Ninio ninio) throws URISyntaxException {
        log.debug("REST request to save Ninio : {}", ninio);
        if (ninio.getId() != null) {
            throw new BadRequestAlertException("A new ninio cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Ninio result = ninioRepository.save(ninio);
        return ResponseEntity
            .created(new URI("/api/ninios/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /ninios/:id} : Updates an existing ninio.
     *
     * @param id the id of the ninio to save.
     * @param ninio the ninio to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ninio,
     * or with status {@code 400 (Bad Request)} if the ninio is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ninio couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ninios/{id}")
    public ResponseEntity<Ninio> updateNinio(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Ninio ninio)
        throws URISyntaxException {
        log.debug("REST request to update Ninio : {}, {}", id, ninio);
        if (ninio.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ninio.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ninioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Ninio result = ninioRepository.save(ninio);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ninio.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /ninios/:id} : Partial updates given fields of an existing ninio, field will ignore if it is null
     *
     * @param id the id of the ninio to save.
     * @param ninio the ninio to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ninio,
     * or with status {@code 400 (Bad Request)} if the ninio is not valid,
     * or with status {@code 404 (Not Found)} if the ninio is not found,
     * or with status {@code 500 (Internal Server Error)} if the ninio couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ninios/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Ninio> partialUpdateNinio(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Ninio ninio
    ) throws URISyntaxException {
        log.debug("REST request to partial update Ninio partially : {}, {}", id, ninio);
        if (ninio.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ninio.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ninioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Ninio> result = ninioRepository
            .findById(ninio.getId())
            .map(existingNinio -> {
                if (ninio.getNombre() != null) {
                    existingNinio.setNombre(ninio.getNombre());
                }
                if (ninio.getApellido() != null) {
                    existingNinio.setApellido(ninio.getApellido());
                }
                if (ninio.getEdad() != null) {
                    existingNinio.setEdad(ninio.getEdad());
                }
                if (ninio.getFechaNacimiento() != null) {
                    existingNinio.setFechaNacimiento(ninio.getFechaNacimiento());
                }

                return existingNinio;
            })
            .map(ninioRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ninio.getId().toString())
        );
    }

    /**
     * {@code GET  /ninios} : get all the ninios.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of ninios in body.
     */
    @GetMapping("/ninios")
    public List<Ninio> getAllNinios() {
        log.debug("REST request to get all Ninios");
        return ninioRepository.findAll();
    }

    /**
     * {@code GET  /ninios/:id} : get the "id" ninio.
     *
     * @param id the id of the ninio to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ninio, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ninios/{id}")
    public ResponseEntity<Ninio> getNinio(@PathVariable Long id) {
        log.debug("REST request to get Ninio : {}", id);
        Optional<Ninio> ninio = ninioRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(ninio);
    }

    /**
     * {@code DELETE  /ninios/:id} : delete the "id" ninio.
     *
     * @param id the id of the ninio to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/ninios/{id}")
    public ResponseEntity<Void> deleteNinio(@PathVariable Long id) {
        log.debug("REST request to delete Ninio : {}", id);
        ninioRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
