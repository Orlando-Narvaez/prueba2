package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Adulto;
import com.mycompany.myapp.repository.AdultoRepository;
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
 * Integration tests for the {@link AdultoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AdultoResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_APELLIDO = "AAAAAAAAAA";
    private static final String UPDATED_APELLIDO = "BBBBBBBBBB";

    private static final Integer DEFAULT_EDAD = 1;
    private static final Integer UPDATED_EDAD = 2;

    private static final LocalDate DEFAULT_FECHA_NACIMIENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_NACIMIENTO = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/adultos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AdultoRepository adultoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAdultoMockMvc;

    private Adulto adulto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Adulto createEntity(EntityManager em) {
        Adulto adulto = new Adulto()
            .nombre(DEFAULT_NOMBRE)
            .apellido(DEFAULT_APELLIDO)
            .edad(DEFAULT_EDAD)
            .fechaNacimiento(DEFAULT_FECHA_NACIMIENTO);
        return adulto;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Adulto createUpdatedEntity(EntityManager em) {
        Adulto adulto = new Adulto()
            .nombre(UPDATED_NOMBRE)
            .apellido(UPDATED_APELLIDO)
            .edad(UPDATED_EDAD)
            .fechaNacimiento(UPDATED_FECHA_NACIMIENTO);
        return adulto;
    }

    @BeforeEach
    public void initTest() {
        adulto = createEntity(em);
    }

    @Test
    @Transactional
    void createAdulto() throws Exception {
        int databaseSizeBeforeCreate = adultoRepository.findAll().size();
        // Create the Adulto
        restAdultoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adulto)))
            .andExpect(status().isCreated());

        // Validate the Adulto in the database
        List<Adulto> adultoList = adultoRepository.findAll();
        assertThat(adultoList).hasSize(databaseSizeBeforeCreate + 1);
        Adulto testAdulto = adultoList.get(adultoList.size() - 1);
        assertThat(testAdulto.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testAdulto.getApellido()).isEqualTo(DEFAULT_APELLIDO);
        assertThat(testAdulto.getEdad()).isEqualTo(DEFAULT_EDAD);
        assertThat(testAdulto.getFechaNacimiento()).isEqualTo(DEFAULT_FECHA_NACIMIENTO);
    }

    @Test
    @Transactional
    void createAdultoWithExistingId() throws Exception {
        // Create the Adulto with an existing ID
        adulto.setId(1L);

        int databaseSizeBeforeCreate = adultoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAdultoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adulto)))
            .andExpect(status().isBadRequest());

        // Validate the Adulto in the database
        List<Adulto> adultoList = adultoRepository.findAll();
        assertThat(adultoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = adultoRepository.findAll().size();
        // set the field null
        adulto.setNombre(null);

        // Create the Adulto, which fails.

        restAdultoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adulto)))
            .andExpect(status().isBadRequest());

        List<Adulto> adultoList = adultoRepository.findAll();
        assertThat(adultoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkApellidoIsRequired() throws Exception {
        int databaseSizeBeforeTest = adultoRepository.findAll().size();
        // set the field null
        adulto.setApellido(null);

        // Create the Adulto, which fails.

        restAdultoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adulto)))
            .andExpect(status().isBadRequest());

        List<Adulto> adultoList = adultoRepository.findAll();
        assertThat(adultoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAdultos() throws Exception {
        // Initialize the database
        adultoRepository.saveAndFlush(adulto);

        // Get all the adultoList
        restAdultoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(adulto.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].apellido").value(hasItem(DEFAULT_APELLIDO)))
            .andExpect(jsonPath("$.[*].edad").value(hasItem(DEFAULT_EDAD)))
            .andExpect(jsonPath("$.[*].fechaNacimiento").value(hasItem(DEFAULT_FECHA_NACIMIENTO.toString())));
    }

    @Test
    @Transactional
    void getAdulto() throws Exception {
        // Initialize the database
        adultoRepository.saveAndFlush(adulto);

        // Get the adulto
        restAdultoMockMvc
            .perform(get(ENTITY_API_URL_ID, adulto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(adulto.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.apellido").value(DEFAULT_APELLIDO))
            .andExpect(jsonPath("$.edad").value(DEFAULT_EDAD))
            .andExpect(jsonPath("$.fechaNacimiento").value(DEFAULT_FECHA_NACIMIENTO.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAdulto() throws Exception {
        // Get the adulto
        restAdultoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAdulto() throws Exception {
        // Initialize the database
        adultoRepository.saveAndFlush(adulto);

        int databaseSizeBeforeUpdate = adultoRepository.findAll().size();

        // Update the adulto
        Adulto updatedAdulto = adultoRepository.findById(adulto.getId()).get();
        // Disconnect from session so that the updates on updatedAdulto are not directly saved in db
        em.detach(updatedAdulto);
        updatedAdulto.nombre(UPDATED_NOMBRE).apellido(UPDATED_APELLIDO).edad(UPDATED_EDAD).fechaNacimiento(UPDATED_FECHA_NACIMIENTO);

        restAdultoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAdulto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAdulto))
            )
            .andExpect(status().isOk());

        // Validate the Adulto in the database
        List<Adulto> adultoList = adultoRepository.findAll();
        assertThat(adultoList).hasSize(databaseSizeBeforeUpdate);
        Adulto testAdulto = adultoList.get(adultoList.size() - 1);
        assertThat(testAdulto.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testAdulto.getApellido()).isEqualTo(UPDATED_APELLIDO);
        assertThat(testAdulto.getEdad()).isEqualTo(UPDATED_EDAD);
        assertThat(testAdulto.getFechaNacimiento()).isEqualTo(UPDATED_FECHA_NACIMIENTO);
    }

    @Test
    @Transactional
    void putNonExistingAdulto() throws Exception {
        int databaseSizeBeforeUpdate = adultoRepository.findAll().size();
        adulto.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAdultoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, adulto.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(adulto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Adulto in the database
        List<Adulto> adultoList = adultoRepository.findAll();
        assertThat(adultoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAdulto() throws Exception {
        int databaseSizeBeforeUpdate = adultoRepository.findAll().size();
        adulto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdultoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(adulto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Adulto in the database
        List<Adulto> adultoList = adultoRepository.findAll();
        assertThat(adultoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAdulto() throws Exception {
        int databaseSizeBeforeUpdate = adultoRepository.findAll().size();
        adulto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdultoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adulto)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Adulto in the database
        List<Adulto> adultoList = adultoRepository.findAll();
        assertThat(adultoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAdultoWithPatch() throws Exception {
        // Initialize the database
        adultoRepository.saveAndFlush(adulto);

        int databaseSizeBeforeUpdate = adultoRepository.findAll().size();

        // Update the adulto using partial update
        Adulto partialUpdatedAdulto = new Adulto();
        partialUpdatedAdulto.setId(adulto.getId());

        partialUpdatedAdulto.nombre(UPDATED_NOMBRE).apellido(UPDATED_APELLIDO).edad(UPDATED_EDAD).fechaNacimiento(UPDATED_FECHA_NACIMIENTO);

        restAdultoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAdulto.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAdulto))
            )
            .andExpect(status().isOk());

        // Validate the Adulto in the database
        List<Adulto> adultoList = adultoRepository.findAll();
        assertThat(adultoList).hasSize(databaseSizeBeforeUpdate);
        Adulto testAdulto = adultoList.get(adultoList.size() - 1);
        assertThat(testAdulto.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testAdulto.getApellido()).isEqualTo(UPDATED_APELLIDO);
        assertThat(testAdulto.getEdad()).isEqualTo(UPDATED_EDAD);
        assertThat(testAdulto.getFechaNacimiento()).isEqualTo(UPDATED_FECHA_NACIMIENTO);
    }

    @Test
    @Transactional
    void fullUpdateAdultoWithPatch() throws Exception {
        // Initialize the database
        adultoRepository.saveAndFlush(adulto);

        int databaseSizeBeforeUpdate = adultoRepository.findAll().size();

        // Update the adulto using partial update
        Adulto partialUpdatedAdulto = new Adulto();
        partialUpdatedAdulto.setId(adulto.getId());

        partialUpdatedAdulto.nombre(UPDATED_NOMBRE).apellido(UPDATED_APELLIDO).edad(UPDATED_EDAD).fechaNacimiento(UPDATED_FECHA_NACIMIENTO);

        restAdultoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAdulto.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAdulto))
            )
            .andExpect(status().isOk());

        // Validate the Adulto in the database
        List<Adulto> adultoList = adultoRepository.findAll();
        assertThat(adultoList).hasSize(databaseSizeBeforeUpdate);
        Adulto testAdulto = adultoList.get(adultoList.size() - 1);
        assertThat(testAdulto.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testAdulto.getApellido()).isEqualTo(UPDATED_APELLIDO);
        assertThat(testAdulto.getEdad()).isEqualTo(UPDATED_EDAD);
        assertThat(testAdulto.getFechaNacimiento()).isEqualTo(UPDATED_FECHA_NACIMIENTO);
    }

    @Test
    @Transactional
    void patchNonExistingAdulto() throws Exception {
        int databaseSizeBeforeUpdate = adultoRepository.findAll().size();
        adulto.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAdultoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, adulto.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(adulto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Adulto in the database
        List<Adulto> adultoList = adultoRepository.findAll();
        assertThat(adultoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAdulto() throws Exception {
        int databaseSizeBeforeUpdate = adultoRepository.findAll().size();
        adulto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdultoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(adulto))
            )
            .andExpect(status().isBadRequest());

        // Validate the Adulto in the database
        List<Adulto> adultoList = adultoRepository.findAll();
        assertThat(adultoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAdulto() throws Exception {
        int databaseSizeBeforeUpdate = adultoRepository.findAll().size();
        adulto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdultoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(adulto)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Adulto in the database
        List<Adulto> adultoList = adultoRepository.findAll();
        assertThat(adultoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAdulto() throws Exception {
        // Initialize the database
        adultoRepository.saveAndFlush(adulto);

        int databaseSizeBeforeDelete = adultoRepository.findAll().size();

        // Delete the adulto
        restAdultoMockMvc
            .perform(delete(ENTITY_API_URL_ID, adulto.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Adulto> adultoList = adultoRepository.findAll();
        assertThat(adultoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
