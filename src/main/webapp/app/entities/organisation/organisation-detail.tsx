import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './organisation.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const OrganisationDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const organisationEntity = useAppSelector(state => state.organisation.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="organisationDetailsHeading">
          <Translate contentKey="hRganiserApp.organisation.detail.title">Organisation</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{organisationEntity.id}</dd>
          <dt>
            <span id="orgName">
              <Translate contentKey="hRganiserApp.organisation.orgName">Org Name</Translate>
            </span>
          </dt>
          <dd>{organisationEntity.orgName}</dd>
          <dt>
            <Translate contentKey="hRganiserApp.organisation.staff">Staff</Translate>
          </dt>
          <dd>
            {organisationEntity.staff
              ? organisationEntity.staff.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {organisationEntity.staff && i === organisationEntity.staff.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/organisation" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/organisation/${organisationEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default OrganisationDetail;
