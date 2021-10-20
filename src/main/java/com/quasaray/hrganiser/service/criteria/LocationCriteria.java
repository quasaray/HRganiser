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
 * Criteria class for the {@link com.quasaray.hrganiser.domain.Location} entity. This class is used
 * in {@link com.quasaray.hrganiser.web.rest.LocationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /locations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class LocationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter locName;

    private LongFilter employeesId;

    private Boolean distinct;

    public LocationCriteria() {}

    public LocationCriteria(LocationCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.locName = other.locName == null ? null : other.locName.copy();
        this.employeesId = other.employeesId == null ? null : other.employeesId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public LocationCriteria copy() {
        return new LocationCriteria(this);
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

    public StringFilter getLocName() {
        return locName;
    }

    public StringFilter locName() {
        if (locName == null) {
            locName = new StringFilter();
        }
        return locName;
    }

    public void setLocName(StringFilter locName) {
        this.locName = locName;
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
        final LocationCriteria that = (LocationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(locName, that.locName) &&
            Objects.equals(employeesId, that.employeesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, locName, employeesId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LocationCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (locName != null ? "locName=" + locName + ", " : "") +
            (employeesId != null ? "employeesId=" + employeesId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
