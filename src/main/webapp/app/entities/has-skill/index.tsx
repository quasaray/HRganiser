import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import HasSkill from './has-skill';
import HasSkillDetail from './has-skill-detail';
import HasSkillUpdate from './has-skill-update';
import HasSkillDeleteDialog from './has-skill-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={HasSkillUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={HasSkillUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={HasSkillDetail} />
      <ErrorBoundaryRoute path={match.url} component={HasSkill} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={HasSkillDeleteDialog} />
  </>
);

export default Routes;
