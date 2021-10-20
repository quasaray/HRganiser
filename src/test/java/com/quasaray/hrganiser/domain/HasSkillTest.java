package com.quasaray.hrganiser.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.quasaray.hrganiser.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HasSkillTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HasSkill.class);
        HasSkill hasSkill1 = new HasSkill();
        hasSkill1.setId(1L);
        HasSkill hasSkill2 = new HasSkill();
        hasSkill2.setId(hasSkill1.getId());
        assertThat(hasSkill1).isEqualTo(hasSkill2);
        hasSkill2.setId(2L);
        assertThat(hasSkill1).isNotEqualTo(hasSkill2);
        hasSkill1.setId(null);
        assertThat(hasSkill1).isNotEqualTo(hasSkill2);
    }
}
