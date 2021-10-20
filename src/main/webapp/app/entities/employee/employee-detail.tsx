import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './employee.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const EmployeeDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const employeeEntity = useAppSelector(state => state.employee.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="employeeDetailsHeading">
          <Translate contentKey="hRganiserApp.employee.detail.title">Employee</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.id}</dd>
          <dt>
            <span id="firstName">
              <Translate contentKey="hRganiserApp.employee.firstName">First Name</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.firstName}</dd>
          <dt>
            <span id="lastName">
              <Translate contentKey="hRganiserApp.employee.lastName">Last Name</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.lastName}</dd>
          <dt>
            <span id="knownAs">
              <Translate contentKey="hRganiserApp.employee.knownAs">Known As</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.knownAs}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="hRganiserApp.employee.email">Email</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.email}</dd>
          <dt>
            <span id="doj">
              <Translate contentKey="hRganiserApp.employee.doj">Doj</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.doj ? <TextFormat value={employeeEntity.doj} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="dod">
              <Translate contentKey="hRganiserApp.employee.dod">Dod</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.dod ? <TextFormat value={employeeEntity.dod} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="dob">
              <Translate contentKey="hRganiserApp.employee.dob">Dob</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.dob ? <TextFormat value={employeeEntity.dob} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="active">
              <Translate contentKey="hRganiserApp.employee.active">Active</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.active ? 'true' : 'false'}</dd>
          <dt>
            <span id="resume">
              <Translate contentKey="hRganiserApp.employee.resume">Resume</Translate>
            </span>
          </dt>
          <dd>
            {employeeEntity.resume ? (
              <div>
                {employeeEntity.resumeContentType ? (
                  <a onClick={openFile(employeeEntity.resumeContentType, employeeEntity.resume)}>
                    <Translate contentKey="entity.action.open">Open</Translate>&nbsp;
                  </a>
                ) : null}
                <span>
                  {employeeEntity.resumeContentType}, {byteSize(employeeEntity.resume)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="jobLevel">
              <Translate contentKey="hRganiserApp.employee.jobLevel">Job Level</Translate>
            </span>
          </dt>
          <dd>{employeeEntity.jobLevel}</dd>
          <dt>
            <Translate contentKey="hRganiserApp.employee.manager">Manager</Translate>
          </dt>
          <dd>{employeeEntity.manager ? employeeEntity.manager.firstName : ''}</dd>
          <dt>
            <Translate contentKey="hRganiserApp.employee.org">Org</Translate>
          </dt>
          <dd>{employeeEntity.org ? employeeEntity.org.orgName : ''}</dd>
          <dt>
            <Translate contentKey="hRganiserApp.employee.loc">Loc</Translate>
          </dt>
          <dd>{employeeEntity.loc ? employeeEntity.loc.locName : ''}</dd>
          <dt>
            <Translate contentKey="hRganiserApp.employee.dept">Dept</Translate>
          </dt>
          <dd>{employeeEntity.dept ? employeeEntity.dept.departmentName : ''}</dd>
          <dt>
            <Translate contentKey="hRganiserApp.employee.sect">Sect</Translate>
          </dt>
          <dd>{employeeEntity.sect ? employeeEntity.sect.sectorName : ''}</dd>
          <dt>
            <Translate contentKey="hRganiserApp.employee.job">Job</Translate>
          </dt>
          <dd>{employeeEntity.job ? employeeEntity.job.jobTitle : ''}</dd>
        </dl>
        <Button tag={Link} to="/employee" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/employee/${employeeEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default EmployeeDetail;
