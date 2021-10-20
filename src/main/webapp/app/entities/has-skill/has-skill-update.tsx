import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { ISkill } from 'app/shared/model/skill.model';
import { getEntities as getSkills } from 'app/entities/skill/skill.reducer';
import { IEmployee } from 'app/shared/model/employee.model';
import { getEntities as getEmployees } from 'app/entities/employee/employee.reducer';
import { getEntity, updateEntity, createEntity, reset } from './has-skill.reducer';
import { IHasSkill } from 'app/shared/model/has-skill.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { SkillLevel } from 'app/shared/model/enumerations/skill-level.model';

export const HasSkillUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const skills = useAppSelector(state => state.skill.entities);
  const employees = useAppSelector(state => state.employee.entities);
  const hasSkillEntity = useAppSelector(state => state.hasSkill.entity);
  const loading = useAppSelector(state => state.hasSkill.loading);
  const updating = useAppSelector(state => state.hasSkill.updating);
  const updateSuccess = useAppSelector(state => state.hasSkill.updateSuccess);
  const skillLevelValues = Object.keys(SkillLevel);
  const handleClose = () => {
    props.history.push('/has-skill');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getSkills({}));
    dispatch(getEmployees({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...hasSkillEntity,
      ...values,
      skill: skills.find(it => it.id.toString() === values.skill.toString()),
      employee: employees.find(it => it.id.toString() === values.employee.toString()),
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
          level: 'Basic',
          ...hasSkillEntity,
          skill: hasSkillEntity?.skill?.id,
          employee: hasSkillEntity?.employee?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="hRganiserApp.hasSkill.home.createOrEditLabel" data-cy="HasSkillCreateUpdateHeading">
            <Translate contentKey="hRganiserApp.hasSkill.home.createOrEditLabel">Create or edit a HasSkill</Translate>
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
                  id="has-skill-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('hRganiserApp.hasSkill.level')}
                id="has-skill-level"
                name="level"
                data-cy="level"
                type="select"
              >
                {skillLevelValues.map(skillLevel => (
                  <option value={skillLevel} key={skillLevel}>
                    {translate('hRganiserApp.SkillLevel' + skillLevel)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="has-skill-skill"
                name="skill"
                data-cy="skill"
                label={translate('hRganiserApp.hasSkill.skill')}
                type="select"
                required
              >
                <option value="" key="0" />
                {skills
                  ? skills.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="has-skill-employee"
                name="employee"
                data-cy="employee"
                label={translate('hRganiserApp.hasSkill.employee')}
                type="select"
                required
              >
                <option value="" key="0" />
                {employees
                  ? employees.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/has-skill" replace color="info">
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

export default HasSkillUpdate;
