import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './has-skill.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const HasSkillDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const hasSkillEntity = useAppSelector(state => state.hasSkill.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="hasSkillDetailsHeading">
          <Translate contentKey="hRganiserApp.hasSkill.detail.title">HasSkill</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{hasSkillEntity.id}</dd>
          <dt>
            <span id="level">
              <Translate contentKey="hRganiserApp.hasSkill.level">Level</Translate>
            </span>
          </dt>
          <dd>{hasSkillEntity.level}</dd>
          <dt>
            <Translate contentKey="hRganiserApp.hasSkill.skill">Skill</Translate>
          </dt>
          <dd>{hasSkillEntity.skill ? hasSkillEntity.skill.id : ''}</dd>
          <dt>
            <Translate contentKey="hRganiserApp.hasSkill.employee">Employee</Translate>
          </dt>
          <dd>{hasSkillEntity.employee ? hasSkillEntity.employee.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/has-skill" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/has-skill/${hasSkillEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default HasSkillDetail;
