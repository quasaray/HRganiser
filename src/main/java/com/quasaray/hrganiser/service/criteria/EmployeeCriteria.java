package com.quasaray.hrganiser.service.criteria;

import com.quasaray.hrganiser.domain.enumeration.JobLevel;
import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.quasaray.hrganiser.domain.Employee} entity. This class is used
 * in {@link com.quasaray.hrganiser.web.rest.EmployeeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /employees?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class EmployeeCriteria implements Serializable, Criteria {

    /**
     * Class for filtering JobLevel
     */
    public static class JobLevelFilter extends Filter<JobLevel> {

        public JobLevelFilter() {}

        public JobLevelFilter(JobLevelFilter filter) {
            super(filter);
        }

        @Override
        public JobLevelFilter copy() {
            return new JobLevelFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter firstName;

    private StringFilter lastName;

    private StringFilter knownAs;

    private StringFilter email;

    private LocalDateFilter doj;

    private LocalDateFilter dod;

    private LocalDateFilter dob;

    private BooleanFilter active;

    private JobLevelFilter jobLevel;

    private LongFilter skillsId;

    private LongFilter managerId;

    private LongFilter orgId;

    private LongFilter locId;

    private LongFilter deptId;

    private LongFilter sectId;

    private LongFilter jobId;

    private Boolean distinct;

    public EmployeeCriteria() {}

    public EmployeeCriteria(EmployeeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.firstName = other.firstName == null ? null : other.firstName.copy();
        this.lastName = other.lastName == null ? null : other.lastName.copy();
        this.knownAs = other.knownAs == null ? null : other.knownAs.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.doj = other.doj == null ? null : other.doj.copy();
        this.dod = other.dod == null ? null : other.dod.copy();
        this.dob = other.dob == null ? null : other.dob.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.jobLevel = other.jobLevel == null ? null : other.jobLevel.copy();
        this.skillsId = other.skillsId == null ? null : other.skillsId.copy();
        this.managerId = other.managerId == null ? null : other.managerId.copy();
        this.orgId = other.orgId == null ? null : other.orgId.copy();
        this.locId = other.locId == null ? null : other.locId.copy();
        this.deptId = other.deptId == null ? null : other.deptId.copy();
        this.sectId = other.sectId == null ? null : other.sectId.copy();
        this.jobId = other.jobId == null ? null : other.jobId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public EmployeeCriteria copy() {
        return new EmployeeCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getFirstName() {
        return firstName;
    }

    public StringFilter firstName() {
        if (firstName == null) {
            firstName = new StringFilter();
        }
        return firstName;
    }

    public void setFirstName(StringFilter firstName) {
        this.firstName = firstName;
    }

    public StringFilter getLastName() {
        return lastName;
    }

    public StringFilter lastName() {
        if (lastName == null) {
            lastName = new StringFilter();
        }
        return lastName;
    }

    public void setLastName(StringFilter lastName) {
        this.lastName = lastName;
    }

    public StringFilter getKnownAs() {
        return knownAs;
    }

    public StringFilter knownAs() {
        if (knownAs == null) {
            knownAs = new StringFilter();
        }
        return knownAs;
    }

    public void setKnownAs(StringFilter knownAs) {
        this.knownAs = knownAs;
    }

    public StringFilter getEmail() {
        return email;
    }

    public StringFilter email() {
        if (email == null) {
            email = new StringFilter();
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public LocalDateFilter getDoj() {
        return doj;
    }

    public LocalDateFilter doj() {
        if (doj == null) {
            doj = new LocalDateFilter();
        }
        return doj;
    }

    public void setDoj(LocalDateFilter doj) {
        this.doj = doj;
    }

    public LocalDateFilter getDod() {
        return dod;
    }

    public LocalDateFilter dod() {
        if (dod == null) {
            dod = new LocalDateFilter();
        }
        return dod;
    }

    public void setDod(LocalDateFilter dod) {
        this.dod = dod;
    }

    public LocalDateFilter getDob() {
        return dob;
    }

    public LocalDateFilter dob() {
        if (dob == null) {
            dob = new LocalDateFilter();
        }
        return dob;
    }

    public void setDob(LocalDateFilter dob) {
        this.dob = dob;
    }

    public BooleanFilter getActive() {
        return active;
    }

    public BooleanFilter active() {
        if (active == null) {
            active = new BooleanFilter();
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
    }

    public JobLevelFilter getJobLevel() {
        return jobLevel;
    }

    public JobLevelFilter jobLevel() {
        if (jobLevel == null) {
            jobLevel = new JobLevelFilter();
        }
        return jobLevel;
    }

    public void setJobLevel(JobLevelFilter jobLevel) {
        this.jobLevel = jobLevel;
    }

    public LongFilter getSkillsId() {
        return skillsId;
    }

    public LongFilter skillsId() {
        if (skillsId == null) {
            skillsId = new LongFilter();
        }
        return skillsId;
    }

    public void setSkillsId(LongFilter skillsId) {
        this.skillsId = skillsId;
    }

    public LongFilter getManagerId() {
        return managerId;
    }

    public LongFilter managerId() {
        if (managerId == null) {
            managerId = new LongFilter();
        }
        return managerId;
    }

    public void setManagerId(LongFilter managerId) {
        this.managerId = managerId;
    }

    public LongFilter getOrgId() {
        return orgId;
    }

    public LongFilter orgId() {
        if (orgId == null) {
            orgId = new LongFilter();
        }
        return orgId;
    }

    public void setOrgId(LongFilter orgId) {
        this.orgId = orgId;
    }

    public LongFilter getLocId() {
        return locId;
    }

    public LongFilter locId() {
        if (locId == null) {
            locId = new LongFilter();
        }
        return locId;
    }

    public void setLocId(LongFilter locId) {
        this.locId = locId;
    }

    public LongFilter getDeptId() {
        return deptId;
    }

    public LongFilter deptId() {
        if (deptId == null) {
            deptId = new LongFilter();
        }
        return deptId;
    }

    public void setDeptId(LongFilter deptId) {
        this.deptId = deptId;
    }

    public LongFilter getSectId() {
        return sectId;
    }

    public LongFilter sectId() {
        if (sectId == null) {
            sectId = new LongFilter();
        }
        return sectId;
    }

    public void setSectId(LongFilter sectId) {
        this.sectId = sectId;
    }

    public LongFilter getJobId() {
        return jobId;
    }

    public LongFilter jobId() {
        if (jobId == null) {
            jobId = new LongFilter();
        }
        return jobId;
    }

    public void setJobId(LongFilter jobId) {
        this.jobId = jobId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EmployeeCriteria that = (EmployeeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(lastName, that.lastName) &&
            Objects.equals(knownAs, that.knownAs) &&
            Objects.equals(email, that.email) &&
            Objects.equals(doj, that.doj) &&
            Objects.equals(dod, that.dod) &&
            Objects.equals(dob, that.dob) &&
            Objects.equals(active, that.active) &&
            Objects.equals(jobLevel, that.jobLevel) &&
            Objects.equals(skillsId, that.skillsId) &&
            Objects.equals(managerId, that.managerId) &&
            Objects.equals(orgId, that.orgId) &&
            Objects.equals(locId, that.locId) &&
            Objects.equals(deptId, that.deptId) &&
            Objects.equals(sectId, that.sectId) &&
            Objects.equals(jobId, that.jobId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            firstName,
            lastName,
            knownAs,
            email,
            doj,
            dod,
            dob,
            active,
            jobLevel,
            skillsId,
            managerId,
            orgId,
            locId,
            deptId,
            sectId,
            jobId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmployeeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (firstName != null ? "firstName=" + firstName + ", " : "") +
            (lastName != null ? "lastName=" + lastName + ", " : "") +
            (knownAs != null ? "knownAs=" + knownAs + ", " : "") +
            (email != null ? "email=" + email + ", " : "") +
            (doj != null ? "doj=" + doj + ", " : "") +
            (dod != null ? "dod=" + dod + ", " : "") +
            (dob != null ? "dob=" + dob + ", " : "") +
            (active != null ? "active=" + active + ", " : "") +
            (jobLevel != null ? "jobLevel=" + jobLevel + ", " : "") +
            (skillsId != null ? "skillsId=" + skillsId + ", " : "") +
            (managerId != null ? "managerId=" + managerId + ", " : "") +
            (orgId != null ? "orgId=" + orgId + ", " : "") +
            (locId != null ? "locId=" + locId + ", " : "") +
            (deptId != null ? "deptId=" + deptId + ", " : "") +
            (sectId != null ? "sectId=" + sectId + ", " : "") +
            (jobId != null ? "jobId=" + jobId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
