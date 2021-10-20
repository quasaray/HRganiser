package com.quasaray.hrganiser.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.quasaray.hrganiser.domain.enumeration.SkillLevel;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A HasSkill.
 */
@Entity
@Table(name = "has_skill")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "hasskill")
public class HasSkill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private SkillLevel level;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "users" }, allowSetters = true)
    private Skill skill;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "skills", "manager", "org", "loc", "dept", "sect", "job" }, allowSetters = true)
    private Employee employee;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public HasSkill id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SkillLevel getLevel() {
        return this.level;
    }

    public HasSkill level(SkillLevel level) {
        this.setLevel(level);
        return this;
    }

    public void setLevel(SkillLevel level) {
        this.level = level;
    }

    public Skill getSkill() {
        return this.skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public HasSkill skill(Skill skill) {
        this.setSkill(skill);
        return this;
    }

    public Employee getEmployee() {
        return this.employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public HasSkill employee(Employee employee) {
        this.setEmployee(employee);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HasSkill)) {
            return false;
        }
        return id != null && id.equals(((HasSkill) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HasSkill{" +
            "id=" + getId() +
            ", level='" + getLevel() + "'" +
            "}";
    }
}
