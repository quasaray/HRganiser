package com.quasaray.hrganiser.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.quasaray.hrganiser.domain.Organisation} entity. This class is used
 * in {@link com.quasaray.hrganiser.web.rest.OrganisationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /organisations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class OrganisationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter orgName;

    private LongFilter staffId;

    private LongFilter employeesId;

    private Boolean distinct;

    public OrganisationCriteria() {}

    public OrganisationCriteria(OrganisationCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.orgName = other.orgName == null ? null : other.orgName.copy();
        this.staffId = other.staffId == null ? null : other.staffId.copy();
        this.employeesId = other.employeesId == null ? null : other.employeesId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public OrganisationCriteria copy() {
        return new OrganisationCriteria(this);
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

    public StringFilter getOrgName() {
        return orgName;
    }

    public StringFilter orgName() {
        if (orgName == null) {
            orgName = new StringFilter();
        }
        return orgName;
    }

    public void setOrgName(StringFilter orgName) {
        this.orgName = orgName;
    }

    public LongFilter getStaffId() {
        return staffId;
    }

    public LongFilter staffId() {
        if (staffId == null) {
            staffId = new LongFilter();
        }
        return staffId;
    }

    public void setStaffId(LongFilter staffId) {
        this.staffId = staffId;
    }

    public LongFilter getEmployeesId() {
        return employeesId;
    }

    public LongFilter employeesId() {
        if (employeesId == null) {
            employeesId = new LongFilter();
        }
        return employeesId;
    }

    public void setEmployeesId(LongFilter employeesId) {
        this.employeesId = employeesId;
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
        final OrganisationCriteria that = (OrganisationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(orgName, that.orgName) &&
            Objects.equals(staffId, that.staffId) &&
            Objects.equals(employeesId, that.employeesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orgName, staffId, employeesId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrganisationCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (orgName != null ? "orgName=" + orgName + ", " : "") +
            (staffId != null ? "staffId=" + staffId + ", " : "") +
            (employeesId != null ? "employeesId=" + employeesId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
