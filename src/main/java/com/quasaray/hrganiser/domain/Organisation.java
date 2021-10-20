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
 * A Organisation.
 */
@Entity
@Table(name = "organisation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "organisation")
public class Organisation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "org_name", nullable = false)
    private String orgName;

    @ManyToMany
    @JoinTable(
        name = "rel_organisation__staff",
        joinColumns = @JoinColumn(name = "organisation_id"),
        inverseJoinColumns = @JoinColumn(name = "staff_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<User> staff = new HashSet<>();

    @OneToMany(mappedBy = "org")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "skills", "manager", "org", "loc", "dept", "sect", "job" }, allowSetters = true)
    private Set<Employee> employees = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Organisation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgName() {
        return this.orgName;
    }

    public Organisation orgName(String orgName) {
        this.setOrgName(orgName);
        return this;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Set<User> getStaff() {
        return this.staff;
    }

    public void setStaff(Set<User> users) {
        this.staff = users;
    }

    public Organisation staff(Set<User> users) {
        this.setStaff(users);
        return this;
    }

    public Organisation addStaff(User user) {
        this.staff.add(user);
        return this;
    }

    public Organisation removeStaff(User user) {
        this.staff.remove(user);
        return this;
    }

    public Set<Employee> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Set<Employee> employees) {
        if (this.employees != null) {
            this.employees.forEach(i -> i.setOrg(null));
        }
        if (employees != null) {
            employees.forEach(i -> i.setOrg(this));
        }
        this.employees = employees;
    }

    public Organisation employees(Set<Employee> employees) {
        this.setEmployees(employees);
        return this;
    }

    public Organisation addEmployees(Employee employee) {
        this.employees.add(employee);
        employee.setOrg(this);
        return this;
    }

    public Organisation removeEmployees(Employee employee) {
        this.employees.remove(employee);
        employee.setOrg(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Organisation)) {
            return false;
        }
        return id != null && id.equals(((Organisation) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Organisation{" +
            "id=" + getId() +
            ", orgName='" + getOrgName() + "'" +
            "}";
    }
}
