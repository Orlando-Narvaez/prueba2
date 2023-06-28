package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AdultoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Adulto.class);
        Adulto adulto1 = new Adulto();
        adulto1.setId(1L);
        Adulto adulto2 = new Adulto();
        adulto2.setId(adulto1.getId());
        assertThat(adulto1).isEqualTo(adulto2);
        adulto2.setId(2L);
        assertThat(adulto1).isNotEqualTo(adulto2);
        adulto1.setId(null);
        assertThat(adulto1).isNotEqualTo(adulto2);
    }
}
