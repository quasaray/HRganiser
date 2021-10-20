package com.quasaray.hrganiser.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Skill.
 */
@Entity
@Table(name = "skill")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "skill")
public class Skill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "skill_name", nullable = false, unique = true)
    private String skillName;

    @OneToMany(mappedBy = "skill")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "skill", "employee" }, allowSetters = true)
    private Set<HasSkill> users = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Skill id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkillName() {
        return this.skillName;
    }

    public Skill skillName(String skillName) {
        this.setSkillName(skillName);
        return this;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Set<HasSkill> getUsers() {
        return this.users;
    }

    public void setUsers(Set<HasSkill> hasSkills) {
        if (this.users != null) {
            this.users.forEach(i -> i.setSkill(null));
        }
        if (hasSkills != null) {
            hasSkills.forEach(i -> i.setSkill(this));
        }
        this.users = hasSkills;
    }

    public Skill users(Set<HasSkill> hasSkills) {
        this.setUsers(hasSkills);
        return this;
    }

    public Skill addUsers(HasSkill hasSkill) {
        this.users.add(hasSkill);
        hasSkill.setSkill(this);
        return this;
    }

    public Skill removeUsers(HasSkill hasSkill) {
        this.users.remove(hasSkill);
        hasSkill.setSkill(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Skill)) {
            return false;
        }
        return id != null && id.equals(((Skill) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Skill{" +
            "id=" + getId() +
            ", skillName='" + getSkillName() + "'" +
            "}";
    }
}
