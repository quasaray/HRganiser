package com.quasaray.hrganiser.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.quasaray.hrganiser.domain.enumeration.JobLevel;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Employee.
 */
@Entity
@Table(name = "employee")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "employee")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "known_as")
    private String knownAs;

    @Column(name = "email")
    private String email;

    @Column(name = "doj")
    private LocalDate doj;

    @Column(name = "dod")
    private LocalDate dod;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "active")
    private Boolean active;

    @Lob
    @Column(name = "resume")
    private byte[] resume;

    @Column(name = "resume_content_type")
    private String resumeContentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_level")
    private JobLevel jobLevel;

    @OneToMany(mappedBy = "employee")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "skill", "employee" }, allowSetters = true)
    private Set<HasSkill> skills = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "skills", "manager", "org", "loc", "dept", "sect", "job" }, allowSetters = true)
    private Employee manager;

    @ManyToOne
    @JsonIgnoreProperties(value = { "staff", "employees" }, allowSetters = true)
    private Organisation org;

    @ManyToOne
    @JsonIgnoreProperties(value = { "employees" }, allowSetters = true)
    private Location loc;

    @ManyToOne
    @JsonIgnoreProperties(value = { "belongsTo", "employees" }, allowSetters = true)
    private Department dept;

    @ManyToOne
    @JsonIgnoreProperties(value = { "employees", "departments" }, allowSetters = true)
    private Sector sect;

    @ManyToOne
    @JsonIgnoreProperties(value = { "employeds" }, allowSetters = true)
    private Job job;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Employee id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Employee firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Employee lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getKnownAs() {
        return this.knownAs;
    }

    public Employee knownAs(String knownAs) {
        this.setKnownAs(knownAs);
        return this;
    }

    public void setKnownAs(String knownAs) {
        this.knownAs = knownAs;
    }

    public String getEmail() {
        return this.email;
    }

    public Employee email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDoj() {
        return this.doj;
    }

    public Employee doj(LocalDate doj) {
        this.setDoj(doj);
        return this;
    }

    public void setDoj(LocalDate doj) {
        this.doj = doj;
    }

    public LocalDate getDod() {
        return this.dod;
    }

    public Employee dod(LocalDate dod) {
        this.setDod(dod);
        return this;
    }

    public void setDod(LocalDate dod) {
        this.dod = dod;
    }

    public LocalDate getDob() {
        return this.dob;
    }

    public Employee dob(LocalDate dob) {
        this.setDob(dob);
        return this;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Employee active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public byte[] getResume() {
        return this.resume;
    }

    public Employee resume(byte[] resume) {
        this.setResume(resume);
        return this;
    }

    public void setResume(byte[] resume) {
        this.resume = resume;
    }

    public String getResumeContentType() {
        return this.resumeContentType;
    }

    public Employee resumeContentType(String resumeContentType) {
        this.resumeContentType = resumeContentType;
        return this;
    }

    public void setResumeContentType(String resumeContentType) {
        this.resumeContentType = resumeContentType;
    }

    public JobLevel getJobLevel() {
        return this.jobLevel;
    }

    public Employee jobLevel(JobLevel jobLevel) {
        this.setJobLevel(jobLevel);
        return this;
    }

    public void setJobLevel(JobLevel jobLevel) {
        this.jobLevel = jobLevel;
    }

    public Set<HasSkill> getSkills() {
        return this.skills;
    }

    public void setSkills(Set<HasSkill> hasSkills) {
        if (this.skills != null) {
            this.skills.forEach(i -> i.setEmployee(null));
        }
        if (hasSkills != null) {
            hasSkills.forEach(i -> i.setEmployee(this));
        }
        this.skills = hasSkills;
    }

    public Employee skills(Set<HasSkill> hasSkills) {
        this.setSkills(hasSkills);
        return this;
    }

    public Employee addSkills(HasSkill hasSkill) {
        this.skills.add(hasSkill);
        hasSkill.setEmployee(this);
        return this;
    }

    public Employee removeSkills(HasSkill hasSkill) {
        this.skills.remove(hasSkill);
        hasSkill.setEmployee(null);
        return this;
    }

    public Employee getManager() {
        return this.manager;
    }

    public void setManager(Employee employee) {
        this.manager = employee;
    }

    public Employee manager(Employee employee) {
        this.setManager(employee);
        return this;
    }

    public Organisation getOrg() {
        return this.org;
    }

    public void setOrg(Organisation organisation) {
        this.org = organisation;
    }

    public Employee org(Organisation organisation) {
        this.setOrg(organisation);
        return this;
    }

    public Location getLoc() {
        return this.loc;
    }

    public void setLoc(Location location) {
        this.loc = location;
    }

    public Employee loc(Location location) {
        this.setLoc(location);
        return this;
    }

    public Department getDept() {
        return this.dept;
    }

    public void setDept(Department department) {
        this.dept = department;
    }

    public Employee dept(Department department) {
        this.setDept(department);
        return this;
    }

    public Sector getSect() {
        return this.sect;
    }

    public void setSect(Sector sector) {
        this.sect = sector;
    }

    public Employee sect(Sector sector) {
        this.setSect(sector);
        return this;
    }

    public Job getJob() {
        return this.job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Employee job(Job job) {
        this.setJob(job);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee)) {
            return false;
        }
        return id != null && id.equals(((Employee) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Employee{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", knownAs='" + getKnownAs() + "'" +
            ", email='" + getEmail() + "'" +
            ", doj='" + getDoj() + "'" +
            ", dod='" + getDod() + "'" +
            ", dob='" + getDob() + "'" +
            ", active='" + getActive() + "'" +
            ", resume='" + getResume() + "'" +
            ", resumeContentType='" + getResumeContentType() + "'" +
            ", jobLevel='" + getJobLevel() + "'" +
            "}";
    }
}
