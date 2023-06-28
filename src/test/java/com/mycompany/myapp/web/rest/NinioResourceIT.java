package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Ninio;
import com.mycompany.myapp.repository.NinioRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link NinioResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NinioResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_APELLIDO = "AAAAAAAAAA";
    private static final String UPDATED_APELLIDO = "BBBBBBBBBB";

    private static final Integer DEFAULT_EDAD = 1;
    private static final Integer UPDATED_EDAD = 2;

    private static final LocalDate DEFAULT_FECHA_NACIMIENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_NACIMIENTO = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/ninios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NinioRepository ninioRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNinioMockMvc;

    private Ninio ninio;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ninio createEntity(EntityManager em) {
        Ninio ninio = new Ninio()
            .nombre(DEFAULT_NOMBRE)
            .apellido(DEFAULT_APELLIDO)
            .edad(DEFAULT_EDAD)
            .fechaNacimiento(DEFAULT_FECHA_NACIMIENTO);
        return ninio;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ninio createUpdatedEntity(EntityManager em) {
        Ninio ninio = new Ninio()
            .nombre(UPDATED_NOMBRE)
            .apellido(UPDATED_APELLIDO)
            .edad(UPDATED_EDAD)
            .fechaNacimiento(UPDATED_FECHA_NACIMIENTO);
        return ninio;
    }

    @BeforeEach
    public void initTest() {
        ninio = createEntity(em);
    }

    @Test
    @Transactional
    void createNinio() throws Exception {
        int databaseSizeBeforeCreate = ninioRepository.findAll().size();
        // Create the Ninio
        restNinioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ninio)))
            .andExpect(status().isCreated());

        // Validate the Ninio in the database
        List<Ninio> ninioList = ninioRepository.findAll();
        assertThat(ninioList).hasSize(databaseSizeBeforeCreate + 1);
        Ninio testNinio = ninioList.get(ninioList.size() - 1);
        assertThat(testNinio.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testNinio.getApellido()).isEqualTo(DEFAULT_APELLIDO);
        assertThat(testNinio.getEdad()).isEqualTo(DEFAULT_EDAD);
        assertThat(testNinio.getFechaNacimiento()).isEqualTo(DEFAULT_FECHA_NACIMIENTO);
    }

    @Test
    @Transactional
    void createNinioWithExistingId() throws Exception {
        // Create the Ninio with an existing ID
        ninio.setId(1L);

        int databaseSizeBeforeCreate = ninioRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNinioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ninio)))
            .andExpect(status().isBadRequest());

        // Validate the Ninio in the database
        List<Ninio> ninioList = ninioRepository.findAll();
        assertThat(ninioList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = ninioRepository.findAll().size();
        // set the field null
        ninio.setNombre(null);

        // Create the Ninio, which fails.

        restNinioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ninio)))
            .andExpect(status().isBadRequest());

        List<Ninio> ninioList = ninioRepository.findAll();
        assertThat(ninioList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkApellidoIsRequired() throws Exception {
        int databaseSizeBeforeTest = ninioRepository.findAll().size();
        // set the field null
        ninio.setApellido(null);

        // Create the Ninio, which fails.

        restNinioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ninio)))
            .andExpect(status().isBadRequest());

        List<Ninio> ninioList = ninioRepository.findAll();
        assertThat(ninioList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNinios() throws Exception {
        // Initialize the database
        ninioRepository.saveAndFlush(ninio);

        // Get all the ninioList
        restNinioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ninio.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].apellido").value(hasItem(DEFAULT_APELLIDO)))
            .andExpect(jsonPath("$.[*].edad").value(hasItem(DEFAULT_EDAD)))
            .andExpect(jsonPath("$.[*].fechaNacimiento").value(hasItem(DEFAULT_FECHA_NACIMIENTO.toString())));
    }

    @Test
    @Transactional
    void getNinio() throws Exception {
        // Initialize the database
        ninioRepository.saveAndFlush(ninio);

        // Get the ninio
        restNinioMockMvc
            .perform(get(ENTITY_API_URL_ID, ninio.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ninio.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.apellido").value(DEFAULT_APELLIDO))
            .andExpect(jsonPath("$.edad").value(DEFAULT_EDAD))
            .andExpect(jsonPath("$.fechaNacimiento").value(DEFAULT_FECHA_NACIMIENTO.toString()));
    }

    @Test
    @Transactional
    void getNonExistingNinio() throws Exception {
        // Get the ninio
        restNinioMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewNinio() throws Exception {
        // Initialize the database
        ninioRepository.saveAndFlush(ninio);

        int databaseSizeBeforeUpdate = ninioRepository.findAll().size();

        // Update the ninio
        Ninio updatedNinio = ninioRepository.findById(ninio.getId()).get();
        // Disconnect from session so that the updates on updatedNinio are not directly saved in db
        em.detach(updatedNinio);
        updatedNinio.nombre(UPDATED_NOMBRE).apellido(UPDATED_APELLIDO).edad(UPDATED_EDAD).fechaNacimiento(UPDATED_FECHA_NACIMIENTO);

        restNinioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedNinio.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedNinio))
            )
            .andExpect(status().isOk());

        // Validate the Ninio in the database
        List<Ninio> ninioList = ninioRepository.findAll();
        assertThat(ninioList).hasSize(databaseSizeBeforeUpdate);
        Ninio testNinio = ninioList.get(ninioList.size() - 1);
        assertThat(testNinio.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testNinio.getApellido()).isEqualTo(UPDATED_APELLIDO);
        assertThat(testNinio.getEdad()).isEqualTo(UPDATED_EDAD);
        assertThat(testNinio.getFechaNacimiento()).isEqualTo(UPDATED_FECHA_NACIMIENTO);
    }

    @Test
    @Transactional
    void putNonExistingNinio() throws Exception {
        int databaseSizeBeforeUpdate = ninioRepository.findAll().size();
        ninio.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNinioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ninio.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ninio))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ninio in the database
        List<Ninio> ninioList = ninioRepository.findAll();
        assertThat(ninioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNinio() throws Exception {
        int databaseSizeBeforeUpdate = ninioRepository.findAll().size();
        ninio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNinioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ninio))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ninio in the database
        List<Ninio> ninioList = ninioRepository.findAll();
        assertThat(ninioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNinio() throws Exception {
        int databaseSizeBeforeUpdate = ninioRepository.findAll().size();
        ninio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNinioMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ninio)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ninio in the database
        List<Ninio> ninioList = ninioRepository.findAll();
        assertThat(ninioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNinioWithPatch() throws Exception {
        // Initialize the database
        ninioRepository.saveAndFlush(ninio);

        int databaseSizeBeforeUpdate = ninioRepository.findAll().size();

        // Update the ninio using partial update
        Ninio partialUpdatedNinio = new Ninio();
        partialUpdatedNinio.setId(ninio.getId());

        partialUpdatedNinio.nombre(UPDATED_NOMBRE);

        restNinioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNinio.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNinio))
            )
            .andExpect(status().isOk());

        // Validate the Ninio in the database
        List<Ninio> ninioList = ninioRepository.findAll();
        assertThat(ninioList).hasSize(databaseSizeBeforeUpdate);
        Ninio testNinio = ninioList.get(ninioList.size() - 1);
        assertThat(testNinio.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testNinio.getApellido()).isEqualTo(DEFAULT_APELLIDO);
        assertThat(testNinio.getEdad()).isEqualTo(DEFAULT_EDAD);
        assertThat(testNinio.getFechaNacimiento()).isEqualTo(DEFAULT_FECHA_NACIMIENTO);
    }

    @Test
    @Transactional
    void fullUpdateNinioWithPatch() throws Exception {
        // Initialize the database
        ninioRepository.saveAndFlush(ninio);

        int databaseSizeBeforeUpdate = ninioRepository.findAll().size();

        // Update the ninio using partial update
        Ninio partialUpdatedNinio = new Ninio();
        partialUpdatedNinio.setId(ninio.getId());

        partialUpdatedNinio.nombre(UPDATED_NOMBRE).apellido(UPDATED_APELLIDO).edad(UPDATED_EDAD).fechaNacimiento(UPDATED_FECHA_NACIMIENTO);

        restNinioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNinio.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNinio))
            )
            .andExpect(status().isOk());

        // Validate the Ninio in the database
        List<Ninio> ninioList = ninioRepository.findAll();
        assertThat(ninioList).hasSize(databaseSizeBeforeUpdate);
        Ninio testNinio = ninioList.get(ninioList.size() - 1);
        assertThat(testNinio.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testNinio.getApellido()).isEqualTo(UPDATED_APELLIDO);
        assertThat(testNinio.getEdad()).isEqualTo(UPDATED_EDAD);
        assertThat(testNinio.getFechaNacimiento()).isEqualTo(UPDATED_FECHA_NACIMIENTO);
    }

    @Test
    @Transactional
    void patchNonExistingNinio() throws Exception {
        int databaseSizeBeforeUpdate = ninioRepository.findAll().size();
        ninio.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNinioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ninio.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ninio))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ninio in the database
        List<Ninio> ninioList = ninioRepository.findAll();
        assertThat(ninioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNinio() throws Exception {
        int databaseSizeBeforeUpdate = ninioRepository.findAll().size();
        ninio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNinioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ninio))
            )
            .andExpect(status().isBadRequest());

        // Validate the Ninio in the database
        List<Ninio> ninioList = ninioRepository.findAll();
        assertThat(ninioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNinio() throws Exception {
        int databaseSizeBeforeUpdate = ninioRepository.findAll().size();
        ninio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNinioMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(ninio)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Ninio in the database
        List<Ninio> ninioList = ninioRepository.findAll();
        assertThat(ninioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNinio() throws Exception {
        // Initialize the database
        ninioRepository.saveAndFlush(ninio);

        int databaseSizeBeforeDelete = ninioRepository.findAll().size();

        // Delete the ninio
        restNinioMockMvc
            .perform(delete(ENTITY_API_URL_ID, ninio.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Ninio> ninioList = ninioRepository.findAll();
        assertThat(ninioList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
