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
 * A Sector.
 */
@Entity
@Table(name = "sector")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "sector")
public class Sector implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "sector_name", nullable = false)
    private String sectorName;

    @OneToMany(mappedBy = "sect")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "skills", "manager", "org", "loc", "dept", "sect", "job" }, allowSetters = true)
    private Set<Employee> employees = new HashSet<>();

    @OneToMany(mappedBy = "belongsTo")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "belongsTo", "employees" }, allowSetters = true)
    private Set<Department> departments = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Sector id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSectorName() {
        return this.sectorName;
    }

    public Sector sectorName(String sectorName) {
        this.setSectorName(sectorName);
        return this;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public Set<Employee> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Set<Employee> employees) {
        if (this.employees != null) {
            this.employees.forEach(i -> i.setSect(null));
        }
        if (employees != null) {
            employees.forEach(i -> i.setSect(this));
        }
        this.employees = employees;
    }

    public Sector employees(Set<Employee> employees) {
        this.setEmployees(employees);
        return this;
    }

    public Sector addEmployees(Employee employee) {
        this.employees.add(employee);
        employee.setSect(this);
        return this;
    }

    public Sector removeEmployees(Employee employee) {
        this.employees.remove(employee);
        employee.setSect(null);
        return this;
    }

    public Set<Department> getDepartments() {
        return this.departments;
    }

    public void setDepartments(Set<Department> departments) {
        if (this.departments != null) {
            this.departments.forEach(i -> i.setBelongsTo(null));
        }
        if (departments != null) {
            departments.forEach(i -> i.setBelongsTo(this));
        }
        this.departments = departments;
    }

    public Sector departments(Set<Department> departments) {
        this.setDepartments(departments);
        return this;
    }

    public Sector addDepartments(Department department) {
        this.departments.add(department);
        department.setBelongsTo(this);
        return this;
    }

    public Sector removeDepartments(Department department) {
        this.departments.remove(department);
        department.setBelongsTo(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sector)) {
            return false;
        }
        return id != null && id.equals(((Sector) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Sector{" +
            "id=" + getId() +
            ", sectorName='" + getSectorName() + "'" +
            "}";
    }
}
