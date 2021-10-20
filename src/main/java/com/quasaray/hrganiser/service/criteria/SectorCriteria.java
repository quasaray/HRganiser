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
 * Criteria class for the {@link com.quasaray.hrganiser.domain.Sector} entity. This class is used
 * in {@link com.quasaray.hrganiser.web.rest.SectorResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sectors?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class SectorCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter sectorName;

    private LongFilter employeesId;

    private LongFilter departmentsId;

    private Boolean distinct;

    public SectorCriteria() {}

    public SectorCriteria(SectorCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.sectorName = other.sectorName == null ? null : other.sectorName.copy();
        this.employeesId = other.employeesId == null ? null : other.employeesId.copy();
        this.departmentsId = other.departmentsId == null ? null : other.departmentsId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SectorCriteria copy() {
        return new SectorCriteria(this);
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

    public StringFilter getSectorName() {
        return sectorName;
    }

    public StringFilter sectorName() {
        if (sectorName == null) {
            sectorName = new StringFilter();
        }
        return sectorName;
    }

    public void setSectorName(StringFilter sectorName) {
        this.sectorName = sectorName;
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

    public LongFilter getDepartmentsId() {
        return departmentsId;
    }

    public LongFilter departmentsId() {
        if (departmentsId == null) {
            departmentsId = new LongFilter();
        }
        return departmentsId;
    }

    public void setDepartmentsId(LongFilter departmentsId) {
        this.departmentsId = departmentsId;
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
        final SectorCriteria that = (SectorCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(sectorName, that.sectorName) &&
            Objects.equals(employeesId, that.employeesId) &&
            Objects.equals(departmentsId, that.departmentsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sectorName, employeesId, departmentsId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SectorCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (sectorName != null ? "sectorName=" + sectorName + ", " : "") +
            (employeesId != null ? "employeesId=" + employeesId + ", " : "") +
            (departmentsId != null ? "departmentsId=" + departmentsId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
