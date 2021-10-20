import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm, ValidatedBlobField } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntities as getEmployees } from 'app/entities/employee/employee.reducer';
import { IOrganisation } from 'app/shared/model/organisation.model';
import { getEntities as getOrganisations } from 'app/entities/organisation/organisation.reducer';
import { ILocation } from 'app/shared/model/location.model';
import { getEntities as getLocations } from 'app/entities/location/location.reducer';
import { IDepartment } from 'app/shared/model/department.model';
import { getEntities as getDepartments } from 'app/entities/department/department.reducer';
import { ISector } from 'app/shared/model/sector.model';
import { getEntities as getSectors } from 'app/entities/sector/sector.reducer';
import { IJob } from 'app/shared/model/job.model';
import { getEntities as getJobs } from 'app/entities/job/job.reducer';
import { getEntity, updateEntity, createEntity, reset } from './employee.reducer';
import { IEmployee } from 'app/shared/model/employee.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { JobLevel } from 'app/shared/model/enumerations/job-level.model';

export const EmployeeUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const employees = useAppSelector(state => state.employee.entities);
  const organisations = useAppSelector(state => state.organisation.entities);
  const locations = useAppSelector(state => state.location.entities);
  const departments = useAppSelector(state => state.department.entities);
  const sectors = useAppSelector(state => state.sector.entities);
  const jobs = useAppSelector(state => state.job.entities);
  const employeeEntity = useAppSelector(state => state.employee.entity);
  const loading = useAppSelector(state => state.employee.loading);
  const updating = useAppSelector(state => state.employee.updating);
  const updateSuccess = useAppSelector(state => state.employee.updateSuccess);
  const jobLevelValues = Object.keys(JobLevel);
  const handleClose = () => {
    props.history.push('/employee');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getEmployees({}));
    dispatch(getOrganisations({}));
    dispatch(getLocations({}));
    dispatch(getDepartments({}));
    dispatch(getSectors({}));
    dispatch(getJobs({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...employeeEntity,
      ...values,
      manager: employees.find(it => it.id.toString() === values.manager.toString()),
      org: organisations.find(it => it.id.toString() === values.org.toString()),
      loc: locations.find(it => it.id.toString() === values.loc.toString()),
      dept: departments.find(it => it.id.toString() === values.dept.toString()),
      sect: sectors.find(it => it.id.toString() === values.sect.toString()),
      job: jobs.find(it => it.id.toString() === values.job.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          jobLevel: 'Junior',
          ...employeeEntity,
          manager: employeeEntity?.manager?.id,
          org: employeeEntity?.org?.id,
          loc: employeeEntity?.loc?.id,
          dept: employeeEntity?.dept?.id,
          sect: employeeEntity?.sect?.id,
          job: employeeEntity?.job?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="hRganiserApp.employee.home.createOrEditLabel" data-cy="EmployeeCreateUpdateHeading">
            <Translate contentKey="hRganiserApp.employee.home.createOrEditLabel">Create or edit a Employee</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="employee-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('hRganiserApp.employee.firstName')}
                id="employee-firstName"
                name="firstName"
                data-cy="firstName"
                type="text"
              />
              <ValidatedField
                label={translate('hRganiserApp.employee.lastName')}
                id="employee-lastName"
                name="lastName"
                data-cy="lastName"
                type="text"
              />
              <ValidatedField
                label={translate('hRganiserApp.employee.knownAs')}
                id="employee-knownAs"
                name="knownAs"
                data-cy="knownAs"
                type="text"
              />
              <ValidatedField
                label={translate('hRganiserApp.employee.email')}
                id="employee-email"
                name="email"
                data-cy="email"
                type="text"
              />
              <ValidatedField label={translate('hRganiserApp.employee.doj')} id="employee-doj" name="doj" data-cy="doj" type="date" />
              <ValidatedField label={translate('hRganiserApp.employee.dod')} id="employee-dod" name="dod" data-cy="dod" type="date" />
              <ValidatedField label={translate('hRganiserApp.employee.dob')} id="employee-dob" name="dob" data-cy="dob" type="date" />
              <ValidatedField
                label={translate('hRganiserApp.employee.active')}
                id="employee-active"
                name="active"
                data-cy="active"
                check
                type="checkbox"
              />
              <ValidatedBlobField
                label={translate('hRganiserApp.employee.resume')}
                id="employee-resume"
                name="resume"
                data-cy="resume"
                openActionLabel={translate('entity.action.open')}
              />
              <ValidatedField
                label={translate('hRganiserApp.employee.jobLevel')}
                id="employee-jobLevel"
                name="jobLevel"
                data-cy="jobLevel"
                type="select"
              >
                {jobLevelValues.map(jobLevel => (
                  <option value={jobLevel} key={jobLevel}>
                    {translate('hRganiserApp.JobLevel' + jobLevel)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="employee-manager"
                name="manager"
                data-cy="manager"
                label={translate('hRganiserApp.employee.manager')}
                type="select"
              >
                <option value="" key="0" />
                {employees
                  ? employees.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.firstName}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="employee-org" name="org" data-cy="org" label={translate('hRganiserApp.employee.org')} type="select">
                <option value="" key="0" />
                {organisations
                  ? organisations.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.orgName}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="employee-loc" name="loc" data-cy="loc" label={translate('hRganiserApp.employee.loc')} type="select">
                <option value="" key="0" />
                {locations
                  ? locations.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.locName}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="employee-dept" name="dept" data-cy="dept" label={translate('hRganiserApp.employee.dept')} type="select">
                <option value="" key="0" />
                {departments
                  ? departments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.departmentName}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="employee-sect" name="sect" data-cy="sect" label={translate('hRganiserApp.employee.sect')} type="select">
                <option value="" key="0" />
                {sectors
                  ? sectors.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.sectorName}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="employee-job" name="job" data-cy="job" label={translate('hRganiserApp.employee.job')} type="select">
                <option value="" key="0" />
                {jobs
                  ? jobs.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.jobTitle}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/employee" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default EmployeeUpdate;
